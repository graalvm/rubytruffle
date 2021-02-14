# frozen_string_literal: true

# Copyright (c) 2018, 2020 Oracle and/or its affiliates. All rights reserved. This
# code is released under a tri EPL/GPL/LGPL license. You can use it,
# redistribute it and/or modify it under the terms of the:
#
# Eclipse Public License version 2.0, or
# GNU General Public License version 2, or
# GNU Lesser General Public License version 2.1.

module Polyglot

  # stub defined in CoreLibrary
  class UnsupportedMessageError < StandardError
  end

  def self.export(name, value)
    Truffle::Interop.export name, value
  end

  def self.export_method(name)
    Truffle::Interop.export_method name
  end

  def self.import(name)
    Truffle::Interop.import(name)
  end

  def self.import_method(name)
    Truffle::Interop.import_method name
  end

  def self.as_enumerable(object)
    Truffle::Interop.enumerable(object)
  end

  # HasArrayElementsTrait
  module HasArrayElementsTrait

    private def read_array_element(index)
      Truffle::Interop.read_array_element(self, index)
    end

    def [](index)
      index += length if index < 0
      return nil if (index < 0 || index >= length)
      read_array_element(index)
    end

    alias_method :at, :[]

    def first
      read_array_element(0)
    end

    def last
      read_array_element(length - 1)
    end

    def size
      Truffle::Interop.array_size(self)
    end

    alias_method :length, :size

    def count(item = nil)
      seq = 0
      if (!item.nil?)
        each { |o| seq += 1 if item == o }
      elsif block_given?
        each { |o| seq += 1 if yield(o) }
      else
        return size
      end
      seq
    end

    def to_a
      if Truffle::Interop.has_array_elements?(self)
        Truffle::Interop.to_array(self)
      else
        raise RuntimeError
      end
    end

    alias_method :to_ary, :to_a

    def each(*args)
      return enum_for(:each) unless block_given?
      i = 0
      while i < length
        yield read_array_element(i)
        i += 1
      end
    end

    def each_with_index
      return enum_for(:each) unless block_given?
      i = 0
      while i < length
        yield read_array_element(i), i
        i += 1
      end
    end

  end

  # ForeignObject class
  class ForeignObject < Object

    def respond_to?(symbol, include_all = false)
      Truffle::Interop.foreign_respond_to?(self, symbol)
    end

    def class
      Truffle::Interop.foreign_class(self)
    end

    def object_id
      Truffle::Interop.identity_hash_code(self)
    end

    alias_method :__id__, :object_id

    def equal?(other_object)
      Truffle::Interop.identical?(self, other_object)
    end

    def inspect
      Truffle::Interop.foreign_inspect(self)
    end

    def java_class?(klass)
      Truffle::Interop.java_class?(klass)
    end

    def is_a?(klass)
      Truffle::Interop.foreign_is_a?(self, klass)
    end

    alias_method :kind_of?, :is_a?

    def nil?
      Truffle::Interop.null?(self)
    end

    def to_str
      Truffle::Interop.foreign_to_str(self)
    end

    def to_s
      Truffle::Interop.foreign_to_s(self)
    end

    def to_i
      return Truffle::Interop.as_int(self) if Truffle::Interop.fits_in_int?(self)
      return Truffle::Interop.as_long(self) if Truffle::Interop.fits_in_long?(self)
      raise TypeError, "can't convert foreign object to Integer"
    end

    def to_f
      return Truffle::Interop.as_double(self) if Truffle::Interop.fits_in_double?(self)
      return Truffle::Interop.as_long(self).to_f if Truffle::Interop.fits_in_long?(self)
      raise TypeError, "can't convert foreign object to Float"
    end

    def keys
      Truffle::Interop.members(self, false)
    end

    def []=(member, value)
      return Truffle::Interop.write_array_element(self, member, value) if member.is_a? Numeric
      Truffle::Interop.write_member(self, member, value)
    end

    def delete(index)
      Truffle::Interop.remove_array_element(self, index) if index.is_a? Numeric
      Truffle::Interop.remove_member(self, index)
    end

  end

  class ForeignArray < ForeignObject
    include Enumerable
    include HasArrayElementsTrait
  end

end

module Java
  def self.type(name)
    Truffle::Interop.java_type(name)
  end

  def self.import(name)
    name = name.to_s
    simple_name = name.split('.').last
    type = Java.type(name)
    if Object.const_defined?(simple_name)
      current = Object.const_get(simple_name)
      if current.equal?(type)
        # Ignore - it's already set
      else
        raise NameError, "constant #{simple_name} already set"
      end
    else
      Object.const_set simple_name, type
    end
    type
  end

  def self.synchronized(object)
    TruffleRuby.synchronized(object) do
      yield
    end
  end

  # test-unit expects `Java::JavaLang::Throwable` to be resolvable if `::Java` is defined (see assertions.rb).
  # When doing JRuby-style interop, that's a fine assumption. However, we have `::Java` defined for Truffle-style
  # interop and in that case, the assumption does not hold. In order to allow the gem to work properly, we define
  # a dummy `Throwable` class here.
  module JavaLang
    class Throwable; end
  end
end
