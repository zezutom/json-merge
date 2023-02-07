# Mergious

**Mergious** is a Kotlin library that lets you merge JSON objects and arrays.

The library supports several merging modes.

* By default, the merging complies to the [JSON Patch (RFC 6902)](https://datatracker.ietf.org/doc/html/rfc6902/)
  specification.
* Alternatively, you can choose between two custom modes, the names are rather self-explanatory:
    * **Combine mode**
    * **Replace mode**

The library lets you apply any of the merging modes on the entire input objects.
Further, there is a DSL you can use to define different merging modes for different parts of the
merged input JSON objects.

All of the merge modes are described in detail:
* [JSON Patch mode](doc/json-patch.md)
* Combine mode
* Replace mode