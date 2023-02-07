# JSON Patch mode

### Conflicting primitive value is overwritten

Original:

```json
{
  "a": "b"
}
```

Other:

```json
{
  "a": "c"
}
```

Merged:

```json
  {
  "a": "c"
}
```

### Passing _null_ removes the respective field

Original:

```json
{
  "a": "b",
  "c": "d"
}
```

Other:

```json
{
  "a": null
}
```

Merged:

```json
{
  "c": "d"
}
```

### Field with a _null_ value in the original object is preserved

Original:

```json
{
  "a": "b",
  "c": null
}
```

Other:

```json
{
  "a": "d"
}
```

Merged:

```json
{
  "a": "d",
  "c": null
}
```

### A new field with a _null_ value in the other object is dropped
Original:

```json
{
  "a": "b"
}
```

Other:

```json
{
  "a": "d",
  "e": null
}
```

Merged:

```json
{
  "a": "d"
}
```

### The respective field is overwritten regardless of a data type mismatch

Original:

```json
{
  "a": [
    "b"
  ]
}
```

Other:

```json
{
  "a": "c"
}
```

Merged:

```json
{
  "a": "c"
}
```

### An array is always overwritten with the new value

Original:

```json
{
  "a": [
    {
      "b": "c"
    }
  ]
}
```

Other:

```json
{
  "a": [
    "hello",
    "world"
  ]
}
```

Merged:

```json
{
  "a": [
    "hello",
    "world"
  ]
}
```

### The original object can be _nullified_

Original:

```json
{
  "a": "b"
}
```

Other:

```json
null
```

Merged:

```json
null
```

### The original object can be replaced by a primitive value

Original:

```json
{
  "a": "b"
}
```

Other:

```json
"hello world"
```

Merged:

```json
"hello world"
```

### The original object can be replaced by an array

Original:

```json
{
  "a": "b"
}
```

Other:

```json
[
  "a",
  "b"
]
```

Merged:

```json
[
  "a",
  "b"
]
```
