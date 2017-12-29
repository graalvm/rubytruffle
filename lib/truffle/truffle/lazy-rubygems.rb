# Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved. This
# code is released under a tri EPL/GPL/LGPL license. You can use it,
# redistribute it and/or modify it under the terms of the:
#
# Eclipse Public License version 1.0
# GNU General Public License version 2
# GNU Lesser General Public License version 2.1

module Kernel
  # Take this alias name so RubyGems will reuse this copy
  # and skip the method below once RubyGems is loaded.
  alias :gem_original_require :require

  private def require(path)
    begin
      gem_original_require(path)
    rescue LoadError
      require 'rubygems'
      require path
    end
  end

  private def gem(*args)
    require 'rubygems'
    gem(*args)
  end
end

class Object
  autoload :Gem, 'rubygems'
end
