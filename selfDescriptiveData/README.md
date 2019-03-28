This example program shows how one might handle self-descriptive data.

To illustrate this, it uses a custom variant of CSV, called "Typed CSV", where
type information is included in the header row along with the column title. An
example of TypedCSV looks like this:
```
string:Name,int:Age,float:Height
Dan,35,6.25
Chuck,50,5.25
```

This example shows TypedCSV data with three columns "Name", "Age", and
"Height", with types "string", "int", and "float", respectively. The following
two rows contain data that abides by these types. The information in the header
row describes the logical format (name and type) of the following rows, making
TypedCSV logically self-descriptive.

This repo creates a program that uses a DFDL schema to first parse only the
header row. Using the titles and types from the result of that parse, it then
generates another DFDL schema that describes the remainder of the data. It then
parses the rest of the data using the generated schema, resulting in an infoset
specific to that data. It then performs some basic transformations on the XML
(sorting and filtering), which is then unparsed back to the original data
format.

To run the program, execute
```
sbt run
```
