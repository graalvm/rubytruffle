/*
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 1.0
 * GNU General Public License version 2
 * GNU Lesser General Public License version 2.1
 */
package org.truffleruby.launcher.options;

// This file is automatically generated by options.yml with 'jt build options'

import javax.annotation.Generated;

@Generated("tool/generate-options.rb")
public class Options {

    public final String HOME;
    public final String LAUNCHER;
    public final String[] LOAD_PATHS;
    public final String[] REQUIRED_LIBRARIES;
    public final String INLINE_SCRIPT;
    public final String DISPLAYED_FILE_NAME;
    public final boolean READ_RUBYOPT;
    public final boolean IGNORE_LINES_BEFORE_RUBY_SHEBANG;
    public final String ORIGINAL_INPUT_FILE;
    public final String WORKING_DIRECTORY;
    public final boolean DEBUG;
    public final Verbosity VERBOSITY;
    public final boolean FROZEN_STRING_LITERALS;
    public final boolean RUBYGEMS;
    public final boolean PATCHING;
    public final boolean PATCHING_OPENSSL;
    public final boolean DID_YOU_MEAN;
    public final String INTERNAL_ENCODING;
    public final String EXTERNAL_ENCODING;
    public final boolean POLYGLOT_STDIO;
    public final boolean SYNC_STDIO;
    public final boolean PLATFORM_USE_JAVA;
    public final boolean NATIVE_INTERRUPT;
    public final boolean CEXT_LOCK;
    public final boolean TRACE_CALLS;
    public final boolean COVERAGE_GLOBAL;
    public final boolean INLINE_JS;
    public final String CORE_LOAD_PATH;
    public final boolean STDLIB_AS_INTERNAL;
    public final boolean LAZY_TRANSLATION_CORE;
    public final boolean LAZY_TRANSLATION_USER;
    public final boolean LAZY_TRANSLATION_LOG;
    public final int ARRAY_UNINITIALIZED_SIZE;
    public final int ARRAY_SMALL;
    public final int HASH_PACKED_ARRAY_MAX;
    public final boolean ROPE_LAZY_SUBSTRINGS;
    public final boolean ROPE_PRINT_INTERN_STATS;
    public final int ROPE_DEPTH_THRESHOLD;
    public final int GLOBAL_VARIABLE_MAX_INVALIDATIONS;
    public final int DEFAULT_CACHE;
    public final int METHOD_LOOKUP_CACHE;
    public final int DISPATCH_CACHE;
    public final int YIELD_CACHE;
    public final int METHOD_TO_PROC_CACHE;
    public final int IS_A_CACHE;
    public final int BIND_CACHE;
    public final int CONSTANT_CACHE;
    public final int INSTANCE_VARIABLE_CACHE;
    public final int BINDING_LOCAL_VARIABLE_CACHE;
    public final int SYMBOL_TO_PROC_CACHE;
    public final int ALLOCATE_CLASS_CACHE;
    public final int PACK_CACHE;
    public final int UNPACK_CACHE;
    public final int EVAL_CACHE;
    public final int CLASS_CACHE;
    public final int ENCODING_COMPATIBLE_QUERY_CACHE;
    public final int ENCODING_LOADED_CLASSES_CACHE;
    public final int THREAD_CACHE;
    public final int ROPE_CLASS_CACHE;
    public final int INTEROP_CONVERT_CACHE;
    public final int INTEROP_EXECUTE_CACHE;
    public final int INTEROP_READ_CACHE;
    public final int INTEROP_WRITE_CACHE;
    public final int INTEROP_INVOKE_CACHE;
    public final int TIME_FORMAT_CACHE;
    public final int POW_CACHE;
    public final boolean CLONE_DEFAULT;
    public final boolean INLINE_DEFAULT;
    public final boolean CORE_ALWAYS_CLONE;
    public final boolean INLINE_NEEDS_CALLER_FRAME;
    public final boolean YIELD_ALWAYS_CLONE;
    public final boolean YIELD_ALWAYS_INLINE;
    public final boolean METHODMISSING_ALWAYS_CLONE;
    public final boolean METHODMISSING_ALWAYS_INLINE;
    public final boolean CALL_WITH_BLOCK_ALWAYS_CLONE;
    public final int PACK_UNROLL_LIMIT;
    public final int PACK_RECOVER_LOOP_MIN;
    public final boolean EXCEPTIONS_STORE_JAVA;
    public final boolean EXCEPTIONS_PRINT_JAVA;
    public final boolean EXCEPTIONS_PRINT_UNCAUGHT_JAVA;
    public final boolean EXCEPTIONS_PRINT_RUBY_FOR_JAVA;
    public final boolean EXCEPTIONS_TRANSLATE_ASSERT;
    public final boolean BACKTRACES_HIDE_CORE_FILES;
    public final boolean BACKTRACES_INTERLEAVE_JAVA;
    public final int BACKTRACES_LIMIT;
    public final boolean BACKTRACES_OMIT_UNUSED;
    public final boolean BASICOPS_INLINE;
    public final boolean GRAAL_WARNING_UNLESS;
    public final boolean SHARED_OBJECTS_ENABLED;
    public final boolean SHARED_OBJECTS_DEBUG;
    public final boolean SHARED_OBJECTS_FORCE;
    public final boolean SHARED_OBJECTS_SHARE_ALL;
    public final boolean CEXTS_LOG_LOAD;
    public final String[] CEXTS_LIBRARY_REMAP;
    public final boolean LOG_DYNAMIC_CONSTANT_LOOKUP;
    public final boolean OPTIONS_LOG;
    public final boolean LOG_LOAD;
    public final boolean LOG_FEATURE_LOCATION;
    public final int FRAME_VARIABLE_ACCESS_LIMIT;
    
    Options(OptionsBuilder builder) {
        HOME = builder.getOrDefault(OptionsCatalog.HOME);
        LAUNCHER = builder.getOrDefault(OptionsCatalog.LAUNCHER);
        LOAD_PATHS = builder.getOrDefault(OptionsCatalog.LOAD_PATHS);
        REQUIRED_LIBRARIES = builder.getOrDefault(OptionsCatalog.REQUIRED_LIBRARIES);
        INLINE_SCRIPT = builder.getOrDefault(OptionsCatalog.INLINE_SCRIPT);
        DISPLAYED_FILE_NAME = builder.getOrDefault(OptionsCatalog.DISPLAYED_FILE_NAME);
        READ_RUBYOPT = builder.getOrDefault(OptionsCatalog.READ_RUBYOPT);
        IGNORE_LINES_BEFORE_RUBY_SHEBANG = builder.getOrDefault(OptionsCatalog.IGNORE_LINES_BEFORE_RUBY_SHEBANG);
        ORIGINAL_INPUT_FILE = builder.getOrDefault(OptionsCatalog.ORIGINAL_INPUT_FILE);
        WORKING_DIRECTORY = builder.getOrDefault(OptionsCatalog.WORKING_DIRECTORY);
        DEBUG = builder.getOrDefault(OptionsCatalog.DEBUG);
        VERBOSITY = builder.getOrDefault(OptionsCatalog.VERBOSITY);
        FROZEN_STRING_LITERALS = builder.getOrDefault(OptionsCatalog.FROZEN_STRING_LITERALS);
        RUBYGEMS = builder.getOrDefault(OptionsCatalog.RUBYGEMS);
        PATCHING = builder.getOrDefault(OptionsCatalog.PATCHING);
        PATCHING_OPENSSL = builder.getOrDefault(OptionsCatalog.PATCHING_OPENSSL);
        DID_YOU_MEAN = builder.getOrDefault(OptionsCatalog.DID_YOU_MEAN);
        INTERNAL_ENCODING = builder.getOrDefault(OptionsCatalog.INTERNAL_ENCODING);
        EXTERNAL_ENCODING = builder.getOrDefault(OptionsCatalog.EXTERNAL_ENCODING);
        POLYGLOT_STDIO = builder.getOrDefault(OptionsCatalog.POLYGLOT_STDIO);
        SYNC_STDIO = builder.getOrDefault(OptionsCatalog.SYNC_STDIO);
        PLATFORM_USE_JAVA = builder.getOrDefault(OptionsCatalog.PLATFORM_USE_JAVA);
        NATIVE_INTERRUPT = builder.getOrDefault(OptionsCatalog.NATIVE_INTERRUPT);
        CEXT_LOCK = builder.getOrDefault(OptionsCatalog.CEXT_LOCK);
        TRACE_CALLS = builder.getOrDefault(OptionsCatalog.TRACE_CALLS);
        COVERAGE_GLOBAL = builder.getOrDefault(OptionsCatalog.COVERAGE_GLOBAL);
        INLINE_JS = builder.getOrDefault(OptionsCatalog.INLINE_JS);
        CORE_LOAD_PATH = builder.getOrDefault(OptionsCatalog.CORE_LOAD_PATH);
        STDLIB_AS_INTERNAL = builder.getOrDefault(OptionsCatalog.STDLIB_AS_INTERNAL);
        LAZY_TRANSLATION_CORE = builder.getOrDefault(OptionsCatalog.LAZY_TRANSLATION_CORE);
        LAZY_TRANSLATION_USER = builder.getOrDefault(OptionsCatalog.LAZY_TRANSLATION_USER);
        LAZY_TRANSLATION_LOG = builder.getOrDefault(OptionsCatalog.LAZY_TRANSLATION_LOG);
        ARRAY_UNINITIALIZED_SIZE = builder.getOrDefault(OptionsCatalog.ARRAY_UNINITIALIZED_SIZE);
        ARRAY_SMALL = builder.getOrDefault(OptionsCatalog.ARRAY_SMALL);
        HASH_PACKED_ARRAY_MAX = builder.getOrDefault(OptionsCatalog.HASH_PACKED_ARRAY_MAX);
        ROPE_LAZY_SUBSTRINGS = builder.getOrDefault(OptionsCatalog.ROPE_LAZY_SUBSTRINGS);
        ROPE_PRINT_INTERN_STATS = builder.getOrDefault(OptionsCatalog.ROPE_PRINT_INTERN_STATS);
        ROPE_DEPTH_THRESHOLD = builder.getOrDefault(OptionsCatalog.ROPE_DEPTH_THRESHOLD);
        GLOBAL_VARIABLE_MAX_INVALIDATIONS = builder.getOrDefault(OptionsCatalog.GLOBAL_VARIABLE_MAX_INVALIDATIONS);
        DEFAULT_CACHE = builder.getOrDefault(OptionsCatalog.DEFAULT_CACHE);
        METHOD_LOOKUP_CACHE = builder.getOrDefault(OptionsCatalog.METHOD_LOOKUP_CACHE, DEFAULT_CACHE);
        DISPATCH_CACHE = builder.getOrDefault(OptionsCatalog.DISPATCH_CACHE, DEFAULT_CACHE);
        YIELD_CACHE = builder.getOrDefault(OptionsCatalog.YIELD_CACHE, DEFAULT_CACHE);
        METHOD_TO_PROC_CACHE = builder.getOrDefault(OptionsCatalog.METHOD_TO_PROC_CACHE, DEFAULT_CACHE);
        IS_A_CACHE = builder.getOrDefault(OptionsCatalog.IS_A_CACHE, DEFAULT_CACHE);
        BIND_CACHE = builder.getOrDefault(OptionsCatalog.BIND_CACHE, DEFAULT_CACHE);
        CONSTANT_CACHE = builder.getOrDefault(OptionsCatalog.CONSTANT_CACHE, DEFAULT_CACHE);
        INSTANCE_VARIABLE_CACHE = builder.getOrDefault(OptionsCatalog.INSTANCE_VARIABLE_CACHE, DEFAULT_CACHE);
        BINDING_LOCAL_VARIABLE_CACHE = builder.getOrDefault(OptionsCatalog.BINDING_LOCAL_VARIABLE_CACHE, DEFAULT_CACHE);
        SYMBOL_TO_PROC_CACHE = builder.getOrDefault(OptionsCatalog.SYMBOL_TO_PROC_CACHE, DEFAULT_CACHE);
        ALLOCATE_CLASS_CACHE = builder.getOrDefault(OptionsCatalog.ALLOCATE_CLASS_CACHE, DEFAULT_CACHE);
        PACK_CACHE = builder.getOrDefault(OptionsCatalog.PACK_CACHE, DEFAULT_CACHE);
        UNPACK_CACHE = builder.getOrDefault(OptionsCatalog.UNPACK_CACHE, DEFAULT_CACHE);
        EVAL_CACHE = builder.getOrDefault(OptionsCatalog.EVAL_CACHE, DEFAULT_CACHE);
        CLASS_CACHE = builder.getOrDefault(OptionsCatalog.CLASS_CACHE, DEFAULT_CACHE);
        ENCODING_COMPATIBLE_QUERY_CACHE = builder.getOrDefault(OptionsCatalog.ENCODING_COMPATIBLE_QUERY_CACHE, DEFAULT_CACHE);
        ENCODING_LOADED_CLASSES_CACHE = builder.getOrDefault(OptionsCatalog.ENCODING_LOADED_CLASSES_CACHE, DEFAULT_CACHE);
        THREAD_CACHE = builder.getOrDefault(OptionsCatalog.THREAD_CACHE, DEFAULT_CACHE);
        ROPE_CLASS_CACHE = builder.getOrDefault(OptionsCatalog.ROPE_CLASS_CACHE);
        INTEROP_CONVERT_CACHE = builder.getOrDefault(OptionsCatalog.INTEROP_CONVERT_CACHE, DEFAULT_CACHE);
        INTEROP_EXECUTE_CACHE = builder.getOrDefault(OptionsCatalog.INTEROP_EXECUTE_CACHE, DEFAULT_CACHE);
        INTEROP_READ_CACHE = builder.getOrDefault(OptionsCatalog.INTEROP_READ_CACHE, DEFAULT_CACHE);
        INTEROP_WRITE_CACHE = builder.getOrDefault(OptionsCatalog.INTEROP_WRITE_CACHE, DEFAULT_CACHE);
        INTEROP_INVOKE_CACHE = builder.getOrDefault(OptionsCatalog.INTEROP_INVOKE_CACHE, DEFAULT_CACHE);
        TIME_FORMAT_CACHE = builder.getOrDefault(OptionsCatalog.TIME_FORMAT_CACHE, DEFAULT_CACHE);
        POW_CACHE = builder.getOrDefault(OptionsCatalog.POW_CACHE, DEFAULT_CACHE);
        CLONE_DEFAULT = builder.getOrDefault(OptionsCatalog.CLONE_DEFAULT);
        INLINE_DEFAULT = builder.getOrDefault(OptionsCatalog.INLINE_DEFAULT);
        CORE_ALWAYS_CLONE = builder.getOrDefault(OptionsCatalog.CORE_ALWAYS_CLONE, CLONE_DEFAULT);
        INLINE_NEEDS_CALLER_FRAME = builder.getOrDefault(OptionsCatalog.INLINE_NEEDS_CALLER_FRAME, INLINE_DEFAULT);
        YIELD_ALWAYS_CLONE = builder.getOrDefault(OptionsCatalog.YIELD_ALWAYS_CLONE, CLONE_DEFAULT);
        YIELD_ALWAYS_INLINE = builder.getOrDefault(OptionsCatalog.YIELD_ALWAYS_INLINE, INLINE_DEFAULT);
        METHODMISSING_ALWAYS_CLONE = builder.getOrDefault(OptionsCatalog.METHODMISSING_ALWAYS_CLONE, CLONE_DEFAULT);
        METHODMISSING_ALWAYS_INLINE = builder.getOrDefault(OptionsCatalog.METHODMISSING_ALWAYS_INLINE, INLINE_DEFAULT);
        CALL_WITH_BLOCK_ALWAYS_CLONE = builder.getOrDefault(OptionsCatalog.CALL_WITH_BLOCK_ALWAYS_CLONE, CLONE_DEFAULT);
        PACK_UNROLL_LIMIT = builder.getOrDefault(OptionsCatalog.PACK_UNROLL_LIMIT);
        PACK_RECOVER_LOOP_MIN = builder.getOrDefault(OptionsCatalog.PACK_RECOVER_LOOP_MIN);
        EXCEPTIONS_STORE_JAVA = builder.getOrDefault(OptionsCatalog.EXCEPTIONS_STORE_JAVA);
        EXCEPTIONS_PRINT_JAVA = builder.getOrDefault(OptionsCatalog.EXCEPTIONS_PRINT_JAVA);
        EXCEPTIONS_PRINT_UNCAUGHT_JAVA = builder.getOrDefault(OptionsCatalog.EXCEPTIONS_PRINT_UNCAUGHT_JAVA);
        EXCEPTIONS_PRINT_RUBY_FOR_JAVA = builder.getOrDefault(OptionsCatalog.EXCEPTIONS_PRINT_RUBY_FOR_JAVA);
        EXCEPTIONS_TRANSLATE_ASSERT = builder.getOrDefault(OptionsCatalog.EXCEPTIONS_TRANSLATE_ASSERT);
        BACKTRACES_HIDE_CORE_FILES = builder.getOrDefault(OptionsCatalog.BACKTRACES_HIDE_CORE_FILES);
        BACKTRACES_INTERLEAVE_JAVA = builder.getOrDefault(OptionsCatalog.BACKTRACES_INTERLEAVE_JAVA);
        BACKTRACES_LIMIT = builder.getOrDefault(OptionsCatalog.BACKTRACES_LIMIT);
        BACKTRACES_OMIT_UNUSED = builder.getOrDefault(OptionsCatalog.BACKTRACES_OMIT_UNUSED);
        BASICOPS_INLINE = builder.getOrDefault(OptionsCatalog.BASICOPS_INLINE);
        GRAAL_WARNING_UNLESS = builder.getOrDefault(OptionsCatalog.GRAAL_WARNING_UNLESS);
        SHARED_OBJECTS_ENABLED = builder.getOrDefault(OptionsCatalog.SHARED_OBJECTS_ENABLED);
        SHARED_OBJECTS_DEBUG = builder.getOrDefault(OptionsCatalog.SHARED_OBJECTS_DEBUG);
        SHARED_OBJECTS_FORCE = builder.getOrDefault(OptionsCatalog.SHARED_OBJECTS_FORCE);
        SHARED_OBJECTS_SHARE_ALL = builder.getOrDefault(OptionsCatalog.SHARED_OBJECTS_SHARE_ALL);
        CEXTS_LOG_LOAD = builder.getOrDefault(OptionsCatalog.CEXTS_LOG_LOAD);
        CEXTS_LIBRARY_REMAP = builder.getOrDefault(OptionsCatalog.CEXTS_LIBRARY_REMAP);
        LOG_DYNAMIC_CONSTANT_LOOKUP = builder.getOrDefault(OptionsCatalog.LOG_DYNAMIC_CONSTANT_LOOKUP);
        OPTIONS_LOG = builder.getOrDefault(OptionsCatalog.OPTIONS_LOG);
        LOG_LOAD = builder.getOrDefault(OptionsCatalog.LOG_LOAD);
        LOG_FEATURE_LOCATION = builder.getOrDefault(OptionsCatalog.LOG_FEATURE_LOCATION);
        FRAME_VARIABLE_ACCESS_LIMIT = builder.getOrDefault(OptionsCatalog.FRAME_VARIABLE_ACCESS_LIMIT);
    }

    public Object fromDescription(OptionDescription<?> description) {
        switch (description.getName()) {
            case "ruby.home":
                return HOME;
            case "ruby.launcher":
                return LAUNCHER;
            case "ruby.load_paths":
                return LOAD_PATHS;
            case "ruby.required_libraries":
                return REQUIRED_LIBRARIES;
            case "ruby.inline_script":
                return INLINE_SCRIPT;
            case "ruby.displayed_file_name":
                return DISPLAYED_FILE_NAME;
            case "ruby.read_rubyopt":
                return READ_RUBYOPT;
            case "ruby.ignore_lines_before_ruby_shebang":
                return IGNORE_LINES_BEFORE_RUBY_SHEBANG;
            case "ruby.original_input_file":
                return ORIGINAL_INPUT_FILE;
            case "ruby.working_directory":
                return WORKING_DIRECTORY;
            case "ruby.debug":
                return DEBUG;
            case "ruby.verbosity":
                return VERBOSITY;
            case "ruby.frozen_string_literals":
                return FROZEN_STRING_LITERALS;
            case "ruby.rubygems":
                return RUBYGEMS;
            case "ruby.patching":
                return PATCHING;
            case "ruby.patching_openssl":
                return PATCHING_OPENSSL;
            case "ruby.did_you_mean":
                return DID_YOU_MEAN;
            case "ruby.internal_encoding":
                return INTERNAL_ENCODING;
            case "ruby.external_encoding":
                return EXTERNAL_ENCODING;
            case "ruby.ployglot.stdio":
                return POLYGLOT_STDIO;
            case "ruby.sync.stdio":
                return SYNC_STDIO;
            case "ruby.platform.use_java":
                return PLATFORM_USE_JAVA;
            case "ruby.platform.native_interrupt":
                return NATIVE_INTERRUPT;
            case "ruby.cexts.lock":
                return CEXT_LOCK;
            case "ruby.trace.calls":
                return TRACE_CALLS;
            case "ruby.coverage.global":
                return COVERAGE_GLOBAL;
            case "ruby.inline_js":
                return INLINE_JS;
            case "ruby.core.load_path":
                return CORE_LOAD_PATH;
            case "ruby.stdlib_as_internal":
                return STDLIB_AS_INTERNAL;
            case "ruby.lazy_translation.core":
                return LAZY_TRANSLATION_CORE;
            case "ruby.lazy_translation.user":
                return LAZY_TRANSLATION_USER;
            case "ruby.lazy_translation.log":
                return LAZY_TRANSLATION_LOG;
            case "ruby.array.uninitialized_size":
                return ARRAY_UNINITIALIZED_SIZE;
            case "ruby.array.small":
                return ARRAY_SMALL;
            case "ruby.hash.packed_array.max":
                return HASH_PACKED_ARRAY_MAX;
            case "ruby.rope.lazy_substrings":
                return ROPE_LAZY_SUBSTRINGS;
            case "ruby.rope.print_intern_stats":
                return ROPE_PRINT_INTERN_STATS;
            case "ruby.rope.depth_threshold":
                return ROPE_DEPTH_THRESHOLD;
            case "ruby.global_variable.max_invalidations":
                return GLOBAL_VARIABLE_MAX_INVALIDATIONS;
            case "ruby.default_cache":
                return DEFAULT_CACHE;
            case "ruby.method_lookup.cache":
                return METHOD_LOOKUP_CACHE;
            case "ruby.dispatch.cache":
                return DISPATCH_CACHE;
            case "ruby.yield.cache":
                return YIELD_CACHE;
            case "ruby.to_proc.cache":
                return METHOD_TO_PROC_CACHE;
            case "ruby.is_a.cache":
                return IS_A_CACHE;
            case "ruby.bind.cache":
                return BIND_CACHE;
            case "ruby.constant.cache":
                return CONSTANT_CACHE;
            case "ruby.instance_variable.cache":
                return INSTANCE_VARIABLE_CACHE;
            case "ruby.binding_local_variable.cache":
                return BINDING_LOCAL_VARIABLE_CACHE;
            case "ruby.symbol_to_proc.cache":
                return SYMBOL_TO_PROC_CACHE;
            case "ruby.allocate_class.cache":
                return ALLOCATE_CLASS_CACHE;
            case "ruby.pack.cache":
                return PACK_CACHE;
            case "ruby.unpack.cache":
                return UNPACK_CACHE;
            case "ruby.eval.cache":
                return EVAL_CACHE;
            case "ruby.class.cache":
                return CLASS_CACHE;
            case "ruby.encoding_compatible_query.cache":
                return ENCODING_COMPATIBLE_QUERY_CACHE;
            case "ruby.encoding_loaded_classes.cache":
                return ENCODING_LOADED_CLASSES_CACHE;
            case "ruby.thread.cache":
                return THREAD_CACHE;
            case "ruby.rope_class.cache":
                return ROPE_CLASS_CACHE;
            case "ruby.interop.convert.cache":
                return INTEROP_CONVERT_CACHE;
            case "ruby.interop.execute.cache":
                return INTEROP_EXECUTE_CACHE;
            case "ruby.interop.read.cache":
                return INTEROP_READ_CACHE;
            case "ruby.interop.write.cache":
                return INTEROP_WRITE_CACHE;
            case "ruby.interop.invoke.cache":
                return INTEROP_INVOKE_CACHE;
            case "ruby.time.format.cache":
                return TIME_FORMAT_CACHE;
            case "ruby.integer.pow.cache":
                return POW_CACHE;
            case "ruby.clone.default":
                return CLONE_DEFAULT;
            case "ruby.inline.default":
                return INLINE_DEFAULT;
            case "ruby.core.always_clone":
                return CORE_ALWAYS_CLONE;
            case "ruby.inline_needs_caller_frame":
                return INLINE_NEEDS_CALLER_FRAME;
            case "ruby.yield.always_clone":
                return YIELD_ALWAYS_CLONE;
            case "ruby.yield.always_inline":
                return YIELD_ALWAYS_INLINE;
            case "ruby.method_missing.always_clone":
                return METHODMISSING_ALWAYS_CLONE;
            case "ruby.method_missing.always_inline":
                return METHODMISSING_ALWAYS_INLINE;
            case "ruby.call_with_block.always_clone":
                return CALL_WITH_BLOCK_ALWAYS_CLONE;
            case "ruby.pack.unroll":
                return PACK_UNROLL_LIMIT;
            case "ruby.pack.recover":
                return PACK_RECOVER_LOOP_MIN;
            case "ruby.exceptions.store_java":
                return EXCEPTIONS_STORE_JAVA;
            case "ruby.exceptions.print_java":
                return EXCEPTIONS_PRINT_JAVA;
            case "ruby.exceptions.print_uncaught_java":
                return EXCEPTIONS_PRINT_UNCAUGHT_JAVA;
            case "ruby.exceptions.print_ruby_for_java":
                return EXCEPTIONS_PRINT_RUBY_FOR_JAVA;
            case "ruby.exceptions.translate_assert":
                return EXCEPTIONS_TRANSLATE_ASSERT;
            case "ruby.backtraces.hide_core_files":
                return BACKTRACES_HIDE_CORE_FILES;
            case "ruby.backtraces.interleave_java":
                return BACKTRACES_INTERLEAVE_JAVA;
            case "ruby.backtraces.limit":
                return BACKTRACES_LIMIT;
            case "ruby.backtraces.omit_unused":
                return BACKTRACES_OMIT_UNUSED;
            case "ruby.basic_ops.inline":
                return BASICOPS_INLINE;
            case "ruby.graal.warn_unless":
                return GRAAL_WARNING_UNLESS;
            case "ruby.shared.objects":
                return SHARED_OBJECTS_ENABLED;
            case "ruby.shared.objects.debug":
                return SHARED_OBJECTS_DEBUG;
            case "ruby.shared.objects.force":
                return SHARED_OBJECTS_FORCE;
            case "ruby.shared.objects.share_all":
                return SHARED_OBJECTS_SHARE_ALL;
            case "ruby.cexts.log.load":
                return CEXTS_LOG_LOAD;
            case "ruby.cexts.remap":
                return CEXTS_LIBRARY_REMAP;
            case "ruby.constant.dynamic_lookup.log":
                return LOG_DYNAMIC_CONSTANT_LOOKUP;
            case "ruby.options.log":
                return OPTIONS_LOG;
            case "ruby.log.load":
                return LOG_LOAD;
            case "ruby.log.feature_location":
                return LOG_FEATURE_LOCATION;
            case "ruby.frame.variable.access.limit":
                return FRAME_VARIABLE_ACCESS_LIMIT;
            default:
                return null;
        }
    }
}
