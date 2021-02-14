# Copyright (c) 2018, 2019 Oracle and/or its affiliates. All rights reserved. This
# code is released under a tri EPL/GPL/LGPL license. You can use it,
# redistribute it and/or modify it under the terms of the:
#
# Eclipse Public License version 2.0, or
# GNU General Public License version 2, or
# GNU Lesser General Public License version 2.1.

require_relative '../../ruby/spec_helper'

describe Polyglot do

  describe "Access to foreign array as ForeignArray" do

    before do
      @foreign = Truffle::Interop.to_java_array([1, 2, 3])
      @foreign_db = Truffle::Interop.to_java_array([1.4, 2.8, 3.6])
    end

    it "should create a ForeignArray class" do
      @foreign.inspect.should =~ /\A#<Java int\[\]:0x\h+ \[1, 2, 3\]>/
      @foreign.length.should == 3
    end

    it "should index array with #[]" do
      @foreign[0].should == 1
      @foreign[1].should == 2
      @foreign[2].should == 3
      @foreign[5].should == nil
    end

    it "should index array with #at" do
      @foreign.at(0).should == 1
      @foreign.at(1).should == 2
      @foreign.at(3).should == nil
    end

    it "should access the first and last elements with #first and #last" do
      @foreign.first.should == 1
      @foreign.last.should == 3
    end

    it "can call each on the foreign array" do
      @foreign.each_with_index do |val, index|
        @foreign[index].should == val
      end
    end

    it "can receive an enumerator from a @foreign array with #each with no block given" do
      enum = @foreign.each
      enum.class.should == Enumerator
      enum.each_with_index do |val, index|
        @foreign[index].should == val
      end
    end

    it "should allow the use of #take" do
      slice = @foreign.take(2)
      slice[0].should == 1
      slice[1].should == 2
      slice.at(2).should == nil
    end

    it "should allow assignment of array element with #[]=" do
      @foreign[2] = 10
      @foreign[2].should == 10
    end

    it "should allow the use of #next" do
      skip
      # NOT WORKING: enum.next says that the iteration reached an end
      enum = @foreign_db.each
      enum.class.should == Enumerator
      puts enum.next
    end

    it "should count objects in the array" do
      foreign = Truffle::Interop.to_java_array([1, 2, 4, 2])
      foreign.count.should == 4
      foreign.count(2).should == 2
      foreign.count { |x| x % 2 == 0 }.should == 3
    end

    it "should indicate that Foreign" do
      object = Object.new
      Truffle::Interop.has_members?(object).should == true
    end

  end

  describe ".eval(id, code)" do

    it "evals code in Ruby" do
      Polyglot.eval("ruby", "14 + 2").should == 16
    end

    it "raises an ArgumentError for unknown languages" do
      -> {
        Polyglot.eval("unknown-language", "code")
      }.should raise_error(ArgumentError, /No language for id unknown-language found/)
    end

    it "doesn't work with MIME types" do
      -> {
        Polyglot.eval("application/x-ruby", "14 + 2")
      }.should raise_error(ArgumentError, /No language for id application\/x-ruby found/)
    end

    it "evals code in Ruby as UTF-8" do
      Polyglot.eval("ruby", "__ENCODING__.name").should == "UTF-8"
    end

    it "evals code in Ruby as UTF-8 even if it was a different encoding in the past" do
      Polyglot.eval("ruby", "__ENCODING__.name".encode("big5")).should == "UTF-8"
    end

    it "will allow code in Ruby to have a magic comment that does not change the encoding" do
      Polyglot.eval("ruby", "# encoding: utf-8\n__ENCODING__.name").should == "UTF-8"
    end

    it "will allow code in Ruby to have a magic comment that is a subset of UTF-8" do
      Polyglot.eval("ruby", "# encoding: us-ascii\n__ENCODING__.name").should == "US-ASCII"
    end

    it "will not allow code in Ruby to have a magic comment to change the encoding to something not a subset of UTF-8" do
      -> {
        Polyglot.eval("ruby", "# encoding: big5\n__ENCODING__.name")
      }.should raise_error(ArgumentError, /big5 cannot be used as an encoding for a Polyglot API Source/)
    end

  end

  describe ".eval_file(id, path)" do

    it "evals code in Ruby" do
      Polyglot.eval_file("ruby", fixture(__FILE__, "eval_file_id.rb"))
      $eval_file_id.should be_true
    end

    it "returns the returned result of the eval" do
      Polyglot.eval_file("ruby", fixture(__FILE__, "eval_file_return.rb")).should == 14
    end

    it "returns the implicit result of the eval" do
      Polyglot.eval_file("ruby", fixture(__FILE__, "eval_file_value.rb")).should == 14
    end

    it "raises an ArgumentError for unknown languages" do
      -> {
        Polyglot.eval_file("unknown-language", fixture(__FILE__, "eval_file_id.rb"))
      }.should raise_error(ArgumentError, /No language for id unknown-language found/)
    end

    it "doesn't work with MIME types" do
      -> {
        Polyglot.eval_file("application/x-ruby", fixture(__FILE__, "eval_file_id.rb"))
      }.should raise_error(ArgumentError, /No language for id application\/x-ruby found/)
    end

    it "evals code in Ruby as UTF-8" do
      Polyglot.eval_file("ruby", fixture(__FILE__, "no_magic.rb")).should == "UTF-8"
    end

    it "will allow code in Ruby to have a magic comment that does not change the encoding" do
      Polyglot.eval_file("ruby", fixture(__FILE__, "utf8_magic.rb")).should == "UTF-8"
    end

    it "will allow code in Ruby to have a magic comment that is a subset of UTF-8" do
      Polyglot.eval_file("ruby", fixture(__FILE__, "usascii_magic.rb")).should == "US-ASCII"
    end

    it "will not allow code in Ruby to have a magic comment to change the encoding" do
      -> {
        Polyglot.eval_file("ruby", fixture(__FILE__, "big5_magic.rb"))
      }.should raise_error(ArgumentError, /big5 cannot be used as an encoding for a Polyglot API Source/)
    end

  end

  describe ".eval_file(path)" do

    it "evals code in Ruby" do
      Polyglot.eval_file(fixture(__FILE__, "eval_file.rb"))
      $eval_file.should be_true
    end

    it "raises an ArgumentError if the language is not found" do
      path = fixture(__FILE__, "eval_file.invalid")
      -> {
        Polyglot.eval_file(path)
      }.should raise_error(ArgumentError, "Could not find language of file #{path}")
    end

  end

  describe ".as_enumerable" do

    it "evals code in Ruby" do
      enumerable = -> { Polyglot.as_enumerable([1, 2, 3]) }
      enumerable.call.min.should == 1
      enumerable.call.max.should == 3
    end

  end

end
