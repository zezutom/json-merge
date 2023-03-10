# json-merge
![build workflow](https://github.com/zezutom/json-merge/actions/workflows/build.yaml/badge.svg)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=zezutom_json-merge&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=zezutom_json-merge)
[![codecov](https://codecov.io/github/zezutom/json-merge/branch/main/graph/badge.svg?token=QTECLTTOTU)](https://codecov.io/github/zezutom/json-merge)

**json-merge** is a Kotlin library that lets you merge JSON objects and arrays.

The library supports several merging modes.

* [JSON Merge Patch (RFC 7386)](#json-merge-patch). That's the default.
* [Combine mode](#combine-mode)
* [Replace mode](#replace-mode)
* [Selective replacement using JsonPath](#selective-replacement-using-jsonpath)

Check how to work with [merge results](#merge-results-handling) and how to deal with exceptions.

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

## Selective replacement using JsonPath

This mode allows you to search for a property in a JSON object and replace it
with the provided value.

The search expression must be a valid [JsonPath](https://www.ietf.org/archive/id/draft-goessner-dispatch-jsonpath-00.html) expression.
The `find and replace` functionality relies on the awesome [Jayway's JsonPath](https://github.com/json-path/JsonPath) library.

```kotlin
val merger = MergeBuilder().withReplaceMode().build()
val original =
    """
        { "a": "b", "c": { "d": "hello" } }
    """
val result = merger.merge("\$.c.d", original, "hi!")
// result: { "a": "b", "c": { "d": "hi!" } }
```

If the property is not found then the original object remains untouched.

```kotlin
val merger = MergeBuilder().withReplaceMode().build()
val original =
    """
        { "a": "b", "c": { "d": "hello" } }
    """
val result = merger.merge("\$.c.e", original, "hi!")    // `c.e` path does not exist 
// result: { "a": "b", "c": { "d": "hello" } }
```

The replacement value does not need to match the original data type. It can be anything.

```kotlin
val merger = MergeBuilder().withReplaceMode().build()
val original =
    """
        { "a": "b", "c": { "d": "hello" } }
    """
val result = merger.merge("\$.c.d", original, """
  { "some": "other object" }
""")
// result: { "a": "b", "c": { "d": { "some": "other object" } } }
```

## Merge results handling

`json-merge` is designed not to throw exceptions at will. Instead, it consistently
returns a [MergeResult](src/main/kotlin/com/tomaszezula/jsonmerge/MergeResult.kt) object. 
This equips you with flexible options how to parse and interpret the result.

### Option 1: Use the wrapper object
The library uses the core `org.json` library. Raw JSON objects (also arrays or primitive values) are stored
in custom wrapper objects - see [Json](src/main/kotlin/com/tomaszezula/jsonmerge/Json.kt).

```kotlin
import org.json.JSONObject
import com.tomaszezula.jsonmerge.Success
import com.tomaszezula.jsonmerge.Failure
import com.tomaszezula.jsonmerge.JsonObject

val json = JSONObject(
    """
        { "a": "b" }
    """)
val mergeResult = Success(JsonObject(json))
val resultValue = when (mergeResult) {
    is Success -> mergeResult.value         
    is Failure -> mergeResult.throwable
}
// JSONObject(
//    """
//        { "a": "b" }
//    """)
```

### Option 2: Grab a string representation of the underlying value
Unwrap the actual JSON string. This operation can fail.
```kotlin
val json = JSONObject(
    """
        { "a": "b" }
    """)
val jsonString = Success(JsonObject(json)).getOrThrow()
// { "a": "b" }
```

### Option 3: Pretty print a string representation of the underlying value
Unwrap the actual JSON string. This operation can fail.
```kotlin
val json = JSONObject(
    """
        { "a": "b" }
    """)
val jsonString = Success(JsonObject(json)).getOrThrow(prettyPrint=true)
// {
//   "a": "b"
// }
```