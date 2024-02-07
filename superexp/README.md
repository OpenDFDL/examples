## DFDL Schema: superexp

Example schema that illustrates space-explosion problems that are possible in DFDL.

### Introduction

DFDL is not Turing complete, as it cannot compute forever given finite input. 

However, it is possible to create a schema which produces an infoset that is super-exponentially larger than the input. 

This is because there is no forward progress requirement on arrays of bounded size (dfdl:occursCountKind='expression').

There is a [theoretical risk of a Denial of Service](#Denial of Service) attack based on this expansion of data size. 

The real problem is that this space expansion and excessive amount of computation it implies is simply not needed to express data formats; hence, DFDL v1.0 is too powerful. 

Additional restrictions are needed in DFDL to prevent the infoset size from growing non-linearly with the size of the data being parsed. 

### Denial of Service
This space expansion creates problems in that a 
DFDL schema can be crafted such that if used it will so overload the system parsing the data that it will crash, thereby successfully denying service (DoS).

This is analogous to the [Billion Laughs Attack](https://en.wikipedia.org/wiki/Billion_laughs_attack) on XML parsers. However, data cannot carry a DFDL schema with it (as an XML document can carry a DTD), so the risks are lower. 
However, if a nefarious actor is able to inject some element and type definitions into a DFDL schema, then these parts of the schema with the explosion of space/time could be activated by data that triggers use of those elements. 

It would be better if DFDL was simply unable to express this sort of space explosion. 

### Limiting DFDL to Linear 

DFDL has a forward-progress restriction where unbounded-size arrays are required to advance forward in parsing. This prevents the parser equivalent of an infinite loop where the infoset is being built, but the parser is not advancing through the data. 

DFDL implementations are required to keep track of the bit position in the input data at the time the array iteration occurs, and to signal a runtime error if no forward progress is made. 

However, DFDL version 1.0 only requires this on unbounded size arrays. This prevents DFDL from parsing forever. But as the tests for this DFDL schema show, taking finite, though super-exponential, time and space to parse, is just as bad. 

The superexp DFDL schema consumes only 3 bits of input, an integer from 0 to 7, yet constructs an infoset that is finite in size, but that finite size is larger than the number of atoms in the universe.
(For more about super-exponential growth, see [Ackermann's Function](https://en.wikipedia.org/wiki/Ackermann_function))

This is, howeer, only possible by taking advantage that DFDL arrays with dfdl:occursCountKind='expression' are not checked for forward progress.
Extending this forward-progress restriction to all arrays goes a long ways toward solving the problem, 
and provides the assurance that given _n_ bits of data, an array of length at most _O(n)_ can be created. 

The question is whether this is sufficient to achieve our practical goals of preventing a nefarious schema from causing DoS.
This is also a run-time check, and a compile-time check that assures us that a DFDL schema will not require this check, that at least one bit is consumed by array iteration, would be far better.

But assume for a minute we're able to achieve that goal. 
Unfortunately, given a DFDL schema of size _s_, it is possible to create an infoset of size _O(2<sup>s</sup>)_. See the DFDL schema file `exp.dfdl.xsd`. This expansion where a size _s_ schema can create a far larger _O(s<sup>k</sup>)_ or _O(2<sup>s</sup>)_ infoset is referred to as _schema fan out_, or just _fan-out_ for short.
This is unrelated to the size of the input data. For any given schema of size _s_, an infoset of size 2<sup>s</sup> is _just a constant factor_. So if DFDL is restricted to linear (i.e., _O(n)_ where _n_ is the data size) size, that remains _O(n)_ even multiplying by this 2<sup>s</sup> constant factor.

But practically speaking, this could still cause DoS issues as a DFDL schema can describe a data format where for each bit of input, a gigantic tree of size _O(2<sup>s</sup>)_ is created. 

Once again, there is no need for this expressive power in DFDL. No data format needs to create parse result infoset structures that much out of proportion to the size of the data. 

Further restrictions in DFDL are needed, with the goal to allow the parsed result to be at most _O(sn)_ where _s_ is the size of the schema, and _n_ is the size of the data being parsed.
If these restrictions are verifiable at DFDL schema compilation time, that provides assurances that no amount of testing can provide for a large complex DFDL schema. 

#### _O(sn)_ is the right goal practically and theoretically
Consider a complex data format. A given chunk of data may contain a construct corresponding to every aspect of the DFDL schema for that format. 
This can be described as the data _covering_ the schema.
It is true that due to backtracking the amount of infoset data produced by parsing may be smaller than _O(s)_, but if the data covers the schema, then at least _O(s)_ processing steps are required, and for our purposes we don't care that much about whether the excessiveness is in processing time, or both time and space used. Hence, if data can be created which covers the schema s, then that is equivalent to saying there is a lower bound of _o(s)_ (little-O notation means at least s steps/storage).

Now repeat that chunk of data N times. This data is size _O(N)_, yet each chunk requires _o(s)_ processing since the data covers the schema. The result is that the output is _θ(sn)_. (The symbol θ denotes exact bounds - both upper and lower bound.) 

Practically speaking, a large complex messaging data format requires roughly _O(sn)_ behavior if the format is fully described, so this is not a theoretical point of interest. 
Real practical data formats would not be able to be described if _O(sn)_ behavior cannot be expressed.

Hence, restricting DFDL so that the output is at most _O(sn)_ is the best we can possibly achieve without over restricting DFDL.

For any actual data format, the size of the schema is a finite constant; hence, _O(sn)_ is _O(n)_ i.e., truly linear; so we will refer to this goal of _O(sn)_ space/time as _true_ or _practical_ _linear_ behavior. 

### Restricting DFDL to Express Only True Linear _O(sn)_ Behavior 

The challenge is restricting DFDL further, in such a way that non-linear _fan-out_ is impossible, while still allowing reuse of groups and types. 

The key concept is that non-linear fan out occurs when parts of the schema, groups, complexTypes, or global element definitions, are reused. 
If reuses also have a runtime forward progress restriction, then there is no way to achieve fan-out without consuming input data proportional to the number of re-uses. 

The proposed restriction on DFDL is as follows: When a group, complexType, or element definition is reused, those uses must either contain a required construct which consumes input data, or they must be separated by a required construct that consumes input data. 

Insuring this is in some ways analogous to the XML Schema Unique Particle Attribution (UPA) requirement.
It requires some analysis of the composition properties of the schema, along with what is required and what is optional/variable.  

## Mandatory Correspondence of Calculated or Defaulted Elements to Data Consuming Elements

One way to enforce this is as part of the runtime DFDL implementation. An implementation can keep record of each element declaration, and the position in the data since the last time that element declaration was used. 
When an element declaration is reused, the position in the data must be greater. 
If an infoset instance of the element is backtracked, then the position for the corresponding element declaration is cleared. 

This restriction at runtime achieves the goal of preventing the fanout problem, but exhaustively testing a schema is difficult. 

It is far better if we can guarantee this forward progress at schema compilation time. 



