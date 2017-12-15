# Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved. This
# code is released under a tri EPL/GPL/LGPL license. You can use it,
# redistribute it and/or modify it under the terms of the:
#
# Eclipse Public License version 1.0
# GNU General Public License version 2
# GNU Lesser General Public License version 2.1

module Truffle::POSIX
  LIBC = Truffle::Interop.eval('application/x-native', 'default')
  DL_EXT = RbConfig::CONFIG['NATIVE_DLEXT']

  home = Truffle::Boot.ruby_home
  libtruffleposix = "#{home}/lib/cext/truffleposix.#{DL_EXT}" if home
  if home
    LIBTRUFFLEPOSIX = Truffle::Interop.eval('application/x-native', "load '#{libtruffleposix}'")
  else
    LIBTRUFFLEPOSIX = LIBC
  end

  if Rubinius.linux?
    LIBCRYPT = Truffle::Interop.eval('application/x-native', 'load libcrypt.so')
  else
    LIBCRYPT = LIBC
  end

  TYPES = {
    :short => :sint16,
    :ushort => :uint16,
    :int => :sint32,
    :uint => :uint32,
    :long => :sint64,
    :ulong => :uint64,
    :long_long => :sint64,
    :ulong_long => :uint64,
  }

  EINTR = Errno::EINTR::Errno

  def self.to_nfi_type(type)
    if found = TYPES[type]
      found
    elsif typedef = Rubinius::Config.lookup("rbx.platform.typedef.#{type}")
      TYPES[type] = to_nfi_type(typedef.to_sym)
    else
      TYPES[type] = type
    end
  end

  # the actual function is looked up and attached on its fist call
  def self.attach_function(native_name, argument_types, return_type,
                           on: Truffle::POSIX, as: native_name, library: LIBC, blocking: false)

    on.define_singleton_method(as) do |*args|
      Truffle::POSIX.attach_function_eagerly native_name, argument_types, return_type,
                                             on: on, as: as, library: library, blocking: blocking
      __send__ as, *args
    end
  end

  def self.attach_function_eagerly(native_name, argument_types, return_type,
                                   on: Truffle::POSIX, as: native_name, library: LIBC, blocking: false)

    method_name = as

    begin
      func = library[native_name]
    rescue NameError # Missing function
      func = nil
    end

    if func
      string_args = []
      argument_types.each_with_index { |arg_type, i|
        if arg_type == :string
          string_args << i
          argument_types[i] = '[sint8]'
        end
      }
      string_args.freeze

      return_type = to_nfi_type(return_type)
      argument_types = argument_types.map { |type| to_nfi_type(type) }
      bound_func = func.bind("(#{argument_types.join(',')}):#{return_type}")

      on.define_singleton_method(method_name) { |*args|
        string_args.each do |i|
          str = args.fetch(i)
          # TODO CS 14-Nov-17 this involves copying to a Java byte[], and then NFI will copy it again!
          args[i] = Truffle.invoke_primitive :string_to_null_terminated_byte_array, str
        end

        if blocking
          result = Truffle.invoke_primitive :thread_run_blocking_nfi_system_call, -> {
            r = bound_func.call(*args)
            if Integer === r and r == -1 and Errno.errno == EINTR
              undefined
            else
              r
            end
          }
        else
          result = bound_func.call(*args)
        end

        if return_type == :string
          if result.nil?
            result = nil
          else
            ptr = Rubinius::FFI::Pointer.new(Truffle::Interop.as_pointer(result))
            result = ptr.read_string_to_null
          end
        elsif return_type == :pointer
          result = Rubinius::FFI::Pointer.new(Truffle::Interop.as_pointer(result))
        end

        result
      }
    else
      on.define_singleton_method(method_name) { |*|
        raise NotImplementedError, "#{native_name} is not available"
      }
      Truffle.invoke_primitive :method_unimplement, method(method_name)
    end
  end

  # Filesystem-related
  attach_function :access, [:string, :int], :int
  attach_function :chdir, [:string], :int
  attach_function :chmod, [:string, :mode_t], :int
  attach_function :chown, [:string, :uid_t, :gid_t], :int
  attach_function :chroot, [:string], :int
  attach_function :truffleposix_clock_gettime, [:int], :int64_t, library: LIBTRUFFLEPOSIX
  attach_function :close, [:int], :int
  attach_function :closedir, [:pointer], :int
  attach_function :dirfd, [:pointer], :int
  attach_function :dup, [:int], :int
  attach_function :dup2, [:int, :int], :int
  attach_function :fchmod, [:int, :mode_t], :int
  attach_function :fchown, [:int, :uid_t, :gid_t], :int
  attach_function :fcntl, [:int, :int, :int], :int
  attach_function :truffleposix_flock, [:int, :int], :int, library: LIBTRUFFLEPOSIX, blocking: true
  attach_function :truffleposix_fstat, [:int, :pointer], :int, library: LIBTRUFFLEPOSIX
  attach_function :fsync, [:int], :int
  attach_function :ftruncate, [:int, :off_t], :int
  attach_function :getcwd, [:pointer, :size_t], :string
  attach_function :isatty, [:int], :int
  attach_function :lchmod, [:string, :mode_t], :int
  attach_function :link, [:string, :string], :int
  attach_function :lseek, [:int, :off_t, :int], :off_t
  attach_function :truffleposix_lstat, [:string, :pointer], :int, library: LIBTRUFFLEPOSIX
  attach_function :truffleposix_major, [:dev_t], :uint, library: LIBTRUFFLEPOSIX
  attach_function :truffleposix_minor, [:dev_t], :uint, library: LIBTRUFFLEPOSIX
  attach_function :mkdir, [:string, :mode_t], :int
  attach_function :mkfifo, [:string, :mode_t], :int
  attach_function :open, [:string, :int, :mode_t], :int
  attach_function :opendir, [:string], :pointer
  attach_function :pipe, [:pointer], :int
  attach_function :read, [:int, :pointer, :size_t], :ssize_t, blocking: true
  attach_function :readlink, [:string, :pointer, :size_t], :ssize_t
  attach_function :truffleposix_readdir, [:pointer], :string, library: LIBTRUFFLEPOSIX
  attach_function :rename, [:string, :string], :int
  attach_function :truffleposix_rewinddir, [:pointer], :void, library: LIBTRUFFLEPOSIX
  attach_function :rmdir, [:string], :int
  attach_function :seekdir, [:pointer, :long], :void
  attach_function :truffleposix_select, [:int, :pointer, :int, :pointer, :int, :pointer, :long], :int, library: LIBTRUFFLEPOSIX
  attach_function :truffleposix_stat, [:string, :pointer], :int, library: LIBTRUFFLEPOSIX
  attach_function :symlink, [:string, :string], :int
  attach_function :telldir, [:pointer], :long
  attach_function :truncate, [:string, :off_t], :int
  attach_function :umask, [:mode_t], :mode_t
  attach_function :unlink, [:string], :int
  attach_function :truffleposix_utimes, [:string, :long, :int, :long, :int], :int, library: LIBTRUFFLEPOSIX
  attach_function :write, [:int, :pointer, :size_t], :ssize_t, blocking: true

  # Process-related
  attach_function :getegid, [], :gid_t
  attach_function :getgid, [], :gid_t
  attach_function :setresgid, [:gid_t, :gid_t, :gid_t], :int
  attach_function :setregid, [:gid_t, :gid_t], :int
  attach_function :setegid, [:uid_t], :int
  attach_function :setgid, [:gid_t], :int

  attach_function :geteuid, [], :uid_t
  attach_function :getuid, [], :uid_t
  attach_function :setresuid, [:uid_t, :uid_t, :uid_t], :int
  attach_function :setreuid, [:uid_t, :uid_t], :int
  attach_function :setruid, [:uid_t], :int
  attach_function :seteuid, [:uid_t], :int
  attach_function :setuid, [:uid_t], :int

  attach_function :getpid, [], :pid_t
  attach_function :getppid, [], :pid_t
  attach_function :kill, [:pid_t, :int], :int
  attach_function :getpgrp, [], :pid_t
  attach_function :getpgid, [:pid_t], :pid_t
  attach_function :setpgid, [:pid_t, :pid_t], :int
  attach_function :setsid, [], :pid_t

  attach_function :getgroups, [:int, :pointer], :int
  attach_function :setgroups, [:size_t, :pointer], :int

  attach_function :getrlimit, [:int, :pointer], :int
  attach_function :setrlimit, [:int, :pointer], :int
  attach_function :truffleposix_getrusage, [:pointer], :int, library: LIBTRUFFLEPOSIX

  attach_function :truffleposix_getpriority, [:int, :id_t], :int, library: LIBTRUFFLEPOSIX
  attach_function :setpriority, [:int, :id_t, :int], :int

  attach_function :execve, [:string, :pointer, :pointer], :int
  attach_function :truffleposix_posix_spawnp, [:string, :pointer, :pointer, :int, :pointer, :int], :pid_t, library: LIBTRUFFLEPOSIX
  attach_function :truffleposix_waitpid, [:pid_t, :int, :pointer], :pid_t, library: LIBTRUFFLEPOSIX, blocking: true

  # ENV-related
  attach_function :getenv, [:string], :string

  attach_function :setenv, [:string, :string, :int], :int, as: :setenv_native
  def self.setenv(name, value, overwrite)
    Truffle.invoke_primitive :posix_invalidate_env, name
    setenv_native(name, value, overwrite)
  end

  attach_function :unsetenv, [:string], :int, as: :unsetenv_native
  def self.unsetenv(name)
    Truffle.invoke_primitive :posix_invalidate_env, name
    unsetenv_native(name)
  end

  # Other routines
  attach_function :crypt, [:string, :string], :string, library: LIBCRYPT
  attach_function :truffleposix_get_user_home, [:string], :pointer, library: LIBTRUFFLEPOSIX

  # Errno-related
  if Rubinius.linux?
    attach_function :__errno_location, [], :pointer, as: :errno_address
  elsif Rubinius.darwin?
    attach_function :__error, [], :pointer, as: :errno_address
  elsif Rubinius.solaris?
    attach_function :___errno, [], :pointer, as: :errno_address
  else
    raise 'Unsupported platform'
  end

  # Platform-specific
  if Rubinius.darwin?
    attach_function :_NSGetArgv, [], :pointer
  end
end if Truffle::Boot.get_option 'platform.native'

module Truffle::POSIX
  NATIVE = Truffle::Boot.get_option 'platform.native'

  def self.with_array_of_ints(ints)
    if ints.empty?
      yield Rubinius::FFI::Pointer::NULL
    else
      Rubinius::FFI::MemoryPointer.new(:int, ints.size) do |ptr|
        ptr.write_array_of_int(ints)
        yield ptr
      end
    end
  end

  def self.with_array_of_strings_pointer(strings)
    Rubinius::FFI::MemoryPointer.new(:pointer, strings.size + 1) do |ptr|
      pointers = strings.map { |str|
        Rubinius::FFI::MemoryPointer.from_string(str)
      }
      pointers << Rubinius::FFI::Pointer::NULL
      ptr.write_array_of_pointer pointers
      yield(ptr)
    end
  end

  def self.read_string_with_retry(io, count)
    fd = io.descriptor
    errno = 1
    until errno == 0
      (res, errno) = read_string(fd, count)
      return res if errno == 0
      if [Errno::EAGAIN::Errno, Errno::EWOULDBLOCK::Errno].include? errno
        IO.select([io])
      else
        Errno.handle
      end
    end
  end

  def self.read_string_no_retry(io, count)
    fd = io.descriptor
    (res, errno) = read_string(fd, count)
    Errno.handle unless errno == 0
    res
  end

  def self.read_string_nonblock(io, count)
    fd = io.descriptor
    (written, errno) = read_string(fd, count)
    if [Errno::EAGAIN::Errno, Errno::EWOULDBLOCK::Errno].include? errno
      raise IO::EAGAINWaitReadable
    end
    Errno.handle unless errno == 0
    written
  end

  def self.read_string(fd, length)
    buffer = Truffle.invoke_primitive(:io_get_thread_buffer, length)
    bytes_read = read(fd, buffer, length)
    if bytes_read < 0
      return [nil, Errno.errno]
    elsif bytes_read == 0 # EOF
      [nil, 0]
    else
      [buffer.read_string(bytes_read), 0]
    end
  end

  def self.write_string_with_retry(io, string)
    fd = io.descriptor
    written = 0
    errno = 1
    until errno == 0
      (written, errno) = write_string(fd, string, written)
      return written if errno == 0
      if [Errno::EAGAIN::Errno, Errno::EWOULDBLOCK::Errno].include? errno
        IO.select([], [io])
      else
        Errno.handle
      end
    end
  end

  def self.write_string_no_retry(io, string)
    fd = io.descriptor
    (written, errno) = write_string(fd, string)
    Errno.handle unless errno == 0
    written
  end

  def self.write_string_nonblock(io, string)
    fd = io.descriptor
    (written, errno) = write_string(fd, string, 0, true)
    if [Errno::EAGAIN::Errno, Errno::EWOULDBLOCK::Errno].include? errno
      raise IO::EAGAINWaitWritable
    end
    Errno.handle unless errno == 0
    written
  end

  def self.write_string(fd, string, written=0, nonblock=false)
    length = string.bytesize
    buffer = Truffle.invoke_primitive(:io_get_thread_buffer, length)
    buffer.write_string string
    while written < length
      ret = write(fd, buffer + written, length - written)
      if ret < 0
        return [written, Errno.errno]
      end
      written += ret

      # Return immediately if nonblock, as otherwise we could get EAGAIN
      # and the caller cannot know how much was written.
      break if nonblock
    end
    [written, 0]
  end

  if Truffle::Boot.get_option('polyglot.stdio')
    class << self
      alias_method :read_string_native, :read_string
      alias_method :write_string_native, :write_string
    end

    def self.read_string(fd, length)
      if fd == 0
        read = Truffle.invoke_primitive :io_read_polyglot, fd, length
        [read, 0]
      else
        read_string_native(fd, length)
      end
    end

    def self.write_string(fd, string, written=0, nonblock=false)
      if fd == 1 || fd == 2
        # Ignore non-blocking for polyglot writes
        written = Truffle.invoke_primitive :io_write_polyglot, fd, string
        [written, 0]
      else
        write_string_native(fd, string, written, nonblock)
      end
    end
  end
end

if Truffle::Boot.get_option 'platform.native'
  # Initialize errno methods so they do not cause classloading when called later on.
  # Classloading may change the value of errno as a side-effect.
  Errno.errno
  Errno.errno = 0
  Errno.handle
end
