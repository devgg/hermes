Hermes
==============
buuild stuff

Overview
--------------

Hermes is a [YAML][1] based protocol definition language. The initial library is beeing developed in Scala with full Java interoperability in mind.

Language Structure
--------------

The Hermes language uses a small subset of YAML's features. A protocol definition consists of 4 sections divided by hyphens (<code>---</code>). The first two sections consists of simple key value pairs. While the final two sections contain the actual protocol unit definitions.
<br>
<br>
 
**Section 1: Protocol Configuration**

| Name | Description | Type | Default | Optional |
| :--: | ----------- | ---- | :-----: | :------: |
| messageIdSize | the size in bytes of the message id | unsigned int | 1 | &#10004; |

<br>
**Section 2: Header definition**

| Name | Description | Type | Optional |
| ---- | ----------- | ---- | :------: |
| protocolId | the id of the protocol | unsigned int | &#10006; |
| version | protocol version | unsigned int | &#10006; |

<br>
**Section 3: Complex Type Definitions**<br>
Complex type definitions are used to specify data that is contained in multiple of the protocol's units.
A complex type definition consist of a name followed by some fields.

```yaml

<string>: # complex datatype name                           
  - field1 
      ...
  - ...

...
```

<br>
**Section 4: Unit Definitions**<br>
The unit definition's syntax is similar to the syntax of the complex type definitions. The only difference is that a complex data type is defined by its name (string) and a unit is defined by its id (unsigned integer). Each unit definition represents one protocol unit.

```yaml
<unsigned int>: # complex datatype name                           
  - field1 
      ...
  - ...

...
  ```

**Fields***
Each field has a datatype. There are three types of data types. 
**Basic datatypes** need to specify their length with either <code> length_f </code> for a fixed length field or <code> length_v </code> for a variable sized field. Optionally the field repetitions can be defined using either <code> reps_f </code> or <code> reps_v </code>. Basic datatypes should be named according to the follwing syntax: <code>BasicTypeName<optionalEncoding></code> e.g.: <code>BigInteger</code>, <code>String<utf8></code> or <code>MyOwnClass<utf8></code>.

**Complex datatypes** defined beforehand can also be used as the fields datatype. Such a field must not specify a length but can set the repetitions. 

Similarly a field with a **protocol data type** must not specify a length but can specify the number of repetitions. A protocol data type should be named according to the follwing syntax: <code>Protocol<protocolName></code> e.g.: <code>Protocol<Chat></code>






Field description

| Name | Description | Type | 
| ---- | ----------- | ---- | 
| type | the type of the field, may be the name of a basic data type, a previously defined complex data type or another protocol | string |
| length_f | the fixed length of the field in bytes | unsigned int |
| length_v | the number of bytes used to define the length of the field during runtime | unsigned int |
| reps_f | the fixed number of field repetitions in bytes | unsigned int |
| reps_v | the number of bytes used to define the number of repetitions during runtime | unsigned int |

Library Usage
--------------














```java
val source = scala.io.Source.fromFile("src/main/resources/messenger.yaml")
val pi = Protocol(source.getLines mkString "\n")
```


```scala
val source = scala.io.Source.fromFile("src/main/resources/messenger.yaml")
val pi = Protocol(source.getLines mkString "\n")
```
<code>chat.yaml or chat.hermes.yaml</code>
```yaml

---
# Complex Type Definitions #

Person:
  - firstName:
      type: String
      length_f: 7
  - lastName:
      type: String
      length_v: 2
  - initial:
      type: String
      length_v: 2

Family:
  - dad:
      type: Person
      reps_f: 1
```


License
--------------

License
--------------




[1]: http://yaml.org/        "YAML"