Macrame provides facilities for meta-programming in Scala. It exposes three related interfaces: macro, function, and runtime.

Macros
======
Each tool in Macrame is provided as a macro which performs all validation at compile-time.

Functions
=========
Each macro is also provided as a function which operates on `Context`s and `Expr`s. These can be used to build up more complicated macros.

Runtime
=======
A runtime version of each macro is also provided. Whenever possible it is recommended that you use the macro version of a tool.
