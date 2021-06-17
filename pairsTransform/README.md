# DFDL Schema: pairsTransform

This DFDL Schema is a demonstration of how DFDL's computed elements and hidden-groups features
can be used to do significant data transformations including structural changes to the fundamental shape of the data. 

In this case the transformation takes a data format that looks like two lists, the first a list
of latitudes, then a list of longitudes. The result of the transformation is a list of pairs, each
containing a latitude and a longitude.

So the physical represenatation of the data, and the logical presentation of the data are radically different.
This transformation is equivalent to inverting a matrix.

## Conclusions

Asymmetries in DFDL v1.0 parsing and unparsing make it impossible to round-trip the complex transform perfectly. You are not able to unparse to the pair of lists from an infoset containing only the list-of-pairs.

Specifically, the fact that for dfdl:occursCountKind 'expression' the expression is NOT evaluated at unparse time makes it impossible to allocate the unparse-computed hidden group representation.

A similar problem was found for hidden xs:choice groups with the fact that dfdl:choiceDispatchKey is not evaluated at unparse time.

The unparse capabilities of DFDL v1.0 are one of its important innovations that make it far more powerful than prior generation format description languages. While these features were never intended to allow DFDL to become a full transformation language, the fact that large scale transformations are possible, expressed on an XML schema, instead of in a separate template or query language, is of significant interest.

Expermentation with flipping these DFDL v1.0 policies so that occurs-count expressions, and choice dispatch keys ARE computed at unparse time is of interest. 

