# Copyright (c) 2016, 2017 Oracle and/or its affiliates. All rights reserved. This
# code is released under a tri EPL/GPL/LGPL license. You can use it,
# redistribute it and/or modify it under the terms of the:
# 
# Eclipse Public License version 1.0
# GNU General Public License version 2
# GNU Lesser General Public License version 2.1

require_relative '../../ruby/spec_helper'
require_relative 'fixtures/classes'

describe "Truffle::Interop.write" do

  describe "writes an instance variable if given an @name" do
    it "as a symbol" do
      object = Object.new
      Truffle::Interop.write object, :@foo, 14
      object.instance_variable_get(:@foo).should == 14
    end
    
    it "as a string" do
      object = Object.new
      Truffle::Interop.write object, '@foo', 14
      object.instance_variable_get(:@foo).should == 14
    end
  end
  
  describe "calls #[]= if there isn't a method with the same name" do
    it "as a symbol" do
      object = TruffleInteropSpecs::WriteHasIndexSet.new
      Truffle::Interop.write object, :foo, 14
      object.called?.should be_true
      object.key.should == "foo"
    end
    
    it "as a string" do
      object = TruffleInteropSpecs::WriteHasIndexSet.new
      Truffle::Interop.write object, 'foo', 14
      object.called?.should be_true
      object.key.should == "foo"
    end

    it "and converts the name to a Ruby String" do
      object = TruffleInteropSpecs::WriteHasIndexSet.new
      Truffle::Interop.write object, Truffle::Interop.to_java_string('foo'), 14
      object.called?.should be_true
      object.key.should == "foo"
    end
  end
  
  describe "calls a method if there is a method with the same name plus =" do
    it "as a symbol" do
      object = TruffleInteropSpecs::WriteHasMethod.new
      Truffle::Interop.write object, :foo, 14
      object.called?.should be_true
    end
    
    it "as a string" do
      object = TruffleInteropSpecs::WriteHasMethod.new
      Truffle::Interop.write object, 'foo', 14
      object.called?.should be_true
    end
  end

  it "can be used to assign an array" do
    array = [1, 2, 3]
    Truffle::Interop.write array, 1, 14
    array[1].should == 14
  end

  it "can be used to assign a hash" do
    hash = {1 => 2, 3 => 4, 5 => 6}

    Truffle::Interop.write hash, 3, 14
    hash[3].should == 14

    Truffle::Interop.write hash, "foo", "bar"
    hash["foo"].should == "bar"

    Truffle::Interop.write hash, :I, "am"
    hash["I"].should == "am"
  end

  it "raises a NameError when the identifier is not found on a Ruby object" do
    obj = Object.new
    lambda {
      Truffle::Interop.write(obj, Truffle::Interop.to_java_string('foo'), 42)
    }.should raise_error(NameError, /Unknown identifier: foo/) { |e|
      e.receiver.should equal obj
      e.name.should == :foo
    }
  end

  it "raises a NameError when the identifier is not found on a foreign object" do
    foreign = Truffle::Interop.java_array(1, 2, 3)
    lambda { foreign.foo = 42 }.should raise_error(NameError, /Unknown identifier: foo/) { |e|
      e.receiver.equal?(foreign).should == true
      e.name.should == :foo
    }
  end

  it "raises a NameError when the name is not a supported type" do
    lambda { Truffle::Interop.write(Math, Truffle::Debug.foreign_object, 42) }.should raise_error(NameError, /Unknown identifier: /)
  end

end
