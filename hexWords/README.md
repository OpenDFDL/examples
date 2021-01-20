# Hex Words

This is an example intended to illustrate some issues with using Daffodil via its API to parse a stream of data.

## Introduction

Many systems make the mistake of assuming data will arrive already quantized into individual units such as messages. 
This does often happen. 
For example if messages are small enough to always fit in a UDP packet, then one packet may contain one message. 
Data arriving on message-queueing systems is also always a finite buffer size usually holding a single data item. 

However, in many other cases the data is just a continuous stream of binary data, without any sort of recognizable delimiters, and the length of data corresponding to a single data item is hard to determine. 
Knowing the format allows separating the data items, but in essence, that is the ONLY way to separate them.

Hence, the general problem is given: 

- a java.io.InputStream from which data bytes can be read
- a DFDL schema for a data format, about which you know and can assume nothing

Write a utility program that allows reading one data item (aka message aka record) from the stream at a time.

The binary data formats in real-world examples of this can be quite complicated, and that complexity would distract from the point of this example, which is showing how the Daffodil API needs to be used. 
Hence, we invent the Hex Words format, which is simple, yet illustrates the problem.

## Hex Words Example

The hex word example is a toy DFDL schema for a simple format that is just complex enough to force the right usage of the Daffodil API. 
Daffodil must be used to separate the data items (the 'words' in this case), and letting Daffodil own/manage the input stream. 

A hex word is a collection of hex digits that looks like a word. For example these are hex names/nicknames of rock bands:

    ABBA
    ACDC
    BADC0

The reason for choosing hex words is because hex digits are 4 bits long, so a hex word that has an odd number of digits occupies a partial byte.

The hexWords format used here preceeds each hex word by its length, a single digit.

    4ABBA
    4ACDC
    5BADC0

In order to have a reason for some data to be invalid, we restrict that a lone hex digit of 0 is illegal. That is, '0' does not mean "empty string". It's simply illegal. Hence, hex words are minimum length 1 digit, maximum length 15 digits. 

Parsing a stream of these hex words requires that Daffodil be allowed to maintain state about the input stream. Daffodil has to "own" or manage the input stream for the duration of the parsing. 

You might think you can isolate the words separately outside of Daffodil, if the byte sequence just looks like the above concatenated:

     4A BB A4 AC DC 5B AD C0

However, notice that the length nibble is sometimes on the left of a byte, sometimes on the right. So that's tricky enough. But we can make it really clear that you want to use Daffodil even just to separate the words by stating that the bit order is not what was just shown, but is dfdl:bitOrder 'leastSignificantBitFirst', so that the words shown above occupy this string of bytes:

     A4 BB 4A CA CD B5 DA 0C

Many important data standards (mil-std-6016 and mil-std-6017 in particular) use this 'leastSignificantBitFirst' bit order, so this is not at all unusual. 

The point of this additional twist of complexity is to make it non-obvious how to parse this in write ad-hoc code even just to separate the messages. Yet the DFDL schema for this is still very small. 

Because a given hex word might occupy half bytes, this example illustrates that Daffodil must be given control of the input stream so that it can maintain state of the position within a byte where parsing has left off in the input stream, and where it will resume on the next call to parse. While an application can open the stream, and provide it to Daffodil, after that Daffodil manages the stream. 
