# json-merge
![build workflow](https://github.com/zezutom/json-merge/actions/workflows/build.yaml/badge.svg)

**json-merge** is a Kotlin library that lets you merge JSON objects and arrays.

The library supports several merging modes.

* [JSON Merge Patch (RFC 7386)](#json-merge-patch). That's the default.
* [Combine mode](#combine-mode)
* [Replace mode](#replace-mode)

## Json Merge Patch

Adheres to [JSON Merge Patch (RFC 7386)](https://www.rfc-editor.org/rfc/rfc7386). This is the default behaviour.

```kotlin
val merger = MergeBuilder().build()
val original =
    """
           { "a": "b", "c": [1,2,3] }
        """
val other =
    """
           { "a": "B", "c": [4,5,6] }
        """
val result = merger.merge(original, other)
// result: { "a": "B", "c": [4,5,6] }
```

Merge results for different inputs:

```
    ORIGINAL        OTHER            RESULT
   ------------------------------------------
   {"a":"b"}       {"a":"c"}       {"a":"c"}

   {"a":"b"}       {"b":"c"}       {"a":"b",
                                    "b":"c"}

   {"a":"b"}       {"a":null}      {}

   {"a":"b",       {"a":null}      {"b":"c"}
    "b":"c"}

   {"a":["b"]}     {"a":"c"}       {"a":"c"}

   {"a":"c"}       {"a":["b"]}     {"a":["b"]}

   {"a": {         {"a": {         {"a": {
     "b": "c"}       "b": "d",       "b": "d"
   }                 "c": null}      }
                   }               }

   {"a": [         {"a": [1]}      {"a": [1]}
     {"b":"c"}
    ]
   }

   ["a","b"]       ["c","d"]       ["c","d"]

   {"a":"b"}       ["c"]           ["c"]

   {"a":"foo"}     null            null

   {"a":"foo"}     "bar"           "bar"

   {"e":null}      {"a":1}         {"e":null,
                                    "a":1}

   [1,2]           {"a":"b",       {"a":"b"}
                    "c":null}

   {}              {"a":            {"a":
                    {"bb":           {"bb":
                     {"ccc":          {}}}
                      null}}}
```

## Combine Mode

This mode tries to preserve maximum information from both the original and the other object (or array).

```kotlin
val merger = MergeBuilder().withCombineMode().build()
val original =
    """
           { "a": "b" }
        """
val other =
    """
           { "a": "c" }
        """
val result = merger.merge(original, other)
// result: { "a": ["b", "c"] }
```

Merge results for different inputs:

```
    ORIGINAL        OTHER            RESULT
   ------------------------------------------
   {"a":"b"}       {"a":"c"}       {"a":["b","c"]}

   {"a":"b"}       {"b":"c"}       {"a":"b",
                                    "b":"c"}

   {"a":"b"}       {"a":null}      {"a":"b"}

   {"a":"b",       {"a":null}      {"a":"b",
    "b":"c"}                        "b":"c"}
    
   {"a":["b"]}     {"a":"c"}       {"a":["b","c"]}

   {"a":"c"}       {"a":["b"]}     {"a":["b","c"]}

   {"a": {         {"a": {         {"a": {
     "b": "c"}       "b": "d",       "b": "d"
                     "c": null       "c": null
   }}                 }}               }}

   {"a": [         {"a": [1]}      {"a": [1, {"b":"c"}]}
     {"b":"c"}
    ]
   }

   ["a","b"]       ["c","d"]       ["a","b","c","d"]

   {"a":"b"}       ["c"]           ["c"]

   {"a":"foo"}     null            null

   {"a":"foo"}     "bar"           "bar"

   {"e":null}      {"a":1}         {"e":null,
                                    "a":1}

   [1,2]           {"a":"b",       {"a":"b","c":null}
                    "c":null}

   {}              {"a":            {"a":            
                    {"bb":           {"bb":             
                     {"ccc":          {"ccc":           
                      null}}}          null}}}
```

## Replace Mode

This mode simply ensures that the original object or array is fully replaced with the new one.

```kotlin
val merger = MergeBuilder().withReplaceMode().build()
val original =
    """
           { "a": "b", "c": [1,2,3] }
        """
val other =
    """
           { "a": "B", "c": [4,5,6] }
        """
val result = merger.merge(original, other)
// result: { "a": "B", "c": [4,5,6] }
```
