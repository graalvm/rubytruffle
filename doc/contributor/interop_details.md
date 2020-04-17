<!-- Generated by spec/truffle/interop/matrix_spec.rb -->

# Detailed definition of polyglot behaviour is given for

- **`nil`**
- **`false`**
- **`true`**
- **`:symbol`**
- **a `String`**
- **an `Integer`**
- **a `Float`**
- **a `BigDecimal`**
- **an `Object`**
- **a frozen `Object`**
- **a `StructWithValue`** – a `Struct` with one property named `value`
- **a `Class`**
- **a `Hash`**
- **an `Array`**
- **`proc {...}`**
- **`lambda {...}`**
- **a `Method`**
- **a `Truffle::FFI::Pointer`** – an object implementing the polyglot pointer API.
- **polyglot pointer** – an object which implements the `polyglot_*` methods for pointer, which are:
  `polyglot_pointer?`,
  `polyglot_as_pointer`,
  `polyglot_to_native`.
  The methods correspond to messages defined in
  [InteropLibrary](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/interop/InteropLibrary.html).
- **polyglot members** – an object which implements the `polyglot_*` methods for members, which are:
  `polyglot_has_members?`,
  `polyglot_members`,
  `polyglot_read_member`,
  `polyglot_write_member`,
  `polyglot_remove_member`,
  `polyglot_invoke_member`,
  `polyglot_member_readable?`,
  `polyglot_member_modifiable?`,
  `polyglot_member_removable?`,
  `polyglot_member_insertable?`,
  `polyglot_member_invocable?`,
  `polyglot_member_internal?`,
  `polyglot_has_member_read_side_effects?`,
  `polyglot_has_member_write_side_effects?`.
  The methods correspond to messages defined in
  [InteropLibrary](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/interop/InteropLibrary.html).
- **polyglot array** – an object which implements the `polyglot_*` methods for array elements, which are:
  `polyglot_has_array_elements?`,
  `polyglot_array_size`,
  `polyglot_read_array_element`,
  `polyglot_write_array_element`,
  `polyglot_remove_array_element`,
  `polyglot_array_element_readable?`,
  `polyglot_array_element_modifiable?`,
  `polyglot_array_element_insertable?`,
  `polyglot_array_element_removable?`.
  The methods correspond to messages defined in
  [InteropLibrary](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/interop/InteropLibrary.html).
- **polyglot int array** – an object which implements the `polyglot_*` methods for array elements allowing only Integers to be stored

# Behavior of interop messages for Ruby objects

## `null` related messages

When interop message `isNull` is sent
- to **`nil`**
  it returns true.
- otherwise
  it returns false.

## `boolean` related messages

When interop message `isBoolean` is sent
- to **`true`** or **`false`**
  it returns true.
- otherwise
  it returns false.

When interop message `asBoolean` is sent
- to **`true`** or **`false`**
  it returns the receiver.
- otherwise
  it fails with `UnsupportedMessageError`.

## Messages related to executable objects

When interop message `isExecutable` is sent
- to **`proc {...}`**, **`lambda {...}`** or **a `Method`**
  it returns true.
- otherwise
  it returns false.

When interop message `execute` is sent
- to **`proc {...}`**, **`lambda {...}`** or **a `Method`**
  it returns the result of the execution.
- to **`lambda {...}`** or **a `Method`**
  it fails with `ArityException` when the number of arguments is wrong.
- to **`proc {...}`**
  it returns the result of the execution even though the number of arguments is wrong (Ruby behavior).
- otherwise
  it fails with `UnsupportedMessageError`.

## Messages related to pointers

When interop message `isPointer` is sent
- to **a `Truffle::FFI::Pointer`**
  it returns true.
- otherwise
  it returns false.

When interop message `asPointer` is sent
- to **a `Truffle::FFI::Pointer`**
  it returns the pointer address.
- otherwise
  it fails with `UnsupportedMessageError`.

When interop message `toNative` is sent
- to **a `Truffle::FFI::Pointer`** or **polyglot pointer**
  it converts the receiver to native and changes the value of isPointer from false to true if possible.
- otherwise
  it does nothing.

## Array related messages

When interop message `hasArrayElements` is sent
- to **an `Array`**, **polyglot array** or **polyglot int array**
  it returns true.
- otherwise
  it returns false.

When interop message `getArraySize` is sent
- to **an `Array`** or **polyglot array**
  it returns size of the array.
- otherwise
  it fails with `UnsupportedMessageError`.

When interop message `readArrayElement` is sent
- to **an `Array`** or **polyglot array**
  it returns the stored value when it is present at the given valid index (`0 <= index < size`).
- to **an `Array`** or **polyglot array**
  it fails with `InvalidArrayIndexException` when a value is not present at the index or the index is invalid.
- otherwise
  it fails with `UnsupportedMessageError`.

When interop message `writeArrayElement` is sent
- to **an `Array`** or **polyglot array**
  it stores a value at a given index.
- to **an `Array`** or **polyglot array**
  it fails with `InvalidArrayIndexException` when a index is invalid.
- to **polyglot int array**
  it fails with `UnsupportedTypeException` when the value is invalid.
- otherwise
  it fails with `UnsupportedMessageError`.

When interop message `removeArrayElement` is sent
- to **an `Array`** or **polyglot array**
  it removes a value when the value is present at a valid index.
- to **an `Array`** or **polyglot array**
  it fails with `InvalidArrayIndexException` when the value is not present at a valid index.
- otherwise
  it fails with `UnsupportedMessageError`.

When interop message `isArrayElementReadable` is sent
- to **an `Array`** or **polyglot array**
  it returns true if there is a value at the given index.
- to **an `Array`** or **polyglot array**
  it returns false if there is no value at the given index.
- otherwise
  it returns false.

When interop message `isArrayElementModifiable` is sent
- to **an `Array`** or **polyglot array**
  it returns true if there is a value at the given index.
- to **an `Array`** or **polyglot array**
  it returns false if there is no value at the given index.
- otherwise
  it returns false.

When interop message `isArrayElementInsertable` is sent
- to **an `Array`** or **polyglot array**
  it returns true if there is no value at the given index.
- to **an `Array`** or **polyglot array**
  it returns false if there is a value at the given index.
- otherwise
  it returns false.

When interop message `isArrayElementRemovable` is sent
- to **an `Array`** or **polyglot array**
  it returns true if there is a value at the given index.
- to **an `Array`** or **polyglot array**
  it returns false if there is no value at the given index.
- otherwise
  it returns false.

## Members related messages (incomplete)

When interop message `readMember` is sent
- to any non-immediate `Object` like **`:symbol`**, **a `String`**, **a `BigDecimal`**, **an `Object`**, **a frozen `Object`**, **a `StructWithValue`**, **a `Class`**, **a `Hash`**, **an `Array`**, **`proc {...}`**, **`lambda {...}`**, **a `Method`**, **a `Truffle::FFI::Pointer`**, **polyglot pointer** or **polyglot array**
  it returns a method with the given name when the method is defined.
- to any non-immediate `Object` like **`:symbol`**, **a `String`**, **a `BigDecimal`**, **an `Object`**, **a frozen `Object`**, **a `StructWithValue`**, **a `Class`**, **a `Hash`**, **an `Array`**, **`proc {...}`**, **`lambda {...}`**, **a `Method`**, **a `Truffle::FFI::Pointer`**, **polyglot pointer** or **polyglot array**
  it fails with `UnknownIdentifierException` when the method is not defined.
- to any non-immediate `Object` like **a `String`**, **a `BigDecimal`**, **an `Object`**, **a `StructWithValue`**, **a `Class`**, **a `Hash`**, **an `Array`**, **`proc {...}`**, **`lambda {...}`**, **a `Method`**, **a `Truffle::FFI::Pointer`**, **polyglot pointer** or **polyglot array**
  it reads the given instance variable.
- to **polyglot members**
  it reads the value stored with the given name.
- to **a `StructWithValue`**
  it returns the value of the given struct member.
- otherwise
  it fails with `UnsupportedMessageError`.

When interop message `writeMember` is sent
- to any non-immediate non-frozen `Object` like **a `String`**, **a `BigDecimal`**, **an `Object`**, **a `StructWithValue`**, **a `Class`**, **a `Hash`**, **an `Array`**, **`proc {...}`**, **`lambda {...}`**, **a `Method`**, **a `Truffle::FFI::Pointer`**, **polyglot pointer** or **polyglot array**
  it writes the given instance variable.
- to **polyglot members**
  it writes the given value under the given name.
- to **a `StructWithValue`**
  it writes the value to the given struct member.
- to **`:symbol`** or **a frozen `Object`**
  it fails with `UnsupportedMessageError` when the receiver is frozen.
- otherwise
  it fails with `UnsupportedMessageError`.

## Number related messages (missing)

## Instantiation related messages (missing)

## Exception related messages (missing)

## Time related messages (unimplemented)