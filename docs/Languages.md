<!-- (c) https://github.com/MontiCore/monticore -->

# MontiCore Languages - an Overview

[MontiCore](http://www.monticore.de) is a language workbench
with an explicit notion of language components. It uses 
grammars to describe textual DSLs. MontiCore uses an extended 
grammar format that allows to compose language components, 
to inherit, extend, embed
and aggregate language components (see the
[**reference manual**](http://monticore.de/MontiCore_Reference-Manual.2017.pdf)
for details).

A **language component** is mainly represented through the grammar 
describing concrete and abstract syntax of the language plus 
Java-classes implementing specific functionalities plus 
Freemarker-Templates helping to print a model to text.
However, language components are often identified with their main 
component grammar.

Language components are currently organized in two levels:
In this list you mainly find grammars for 
**complete (but also reusable and adaptable) languages**.
A list of
[**grammar components**](../monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
with individual reusable nonterminals is also available in
the MontiCore core project.

The following list presents links to the language development projects, their
main grammars, and a short description 
of the language, available language tools and its development status.
The different development stati for grammars are explained 
[**here**](../00.org/Explanations/StatusOfGrammars.md).

The list covers the language grammars to be found in the several 
`MontiCore` projects, such as `cd4analysis/cd4analysis`
usually in folders like `src/main/grammars/` organized in packages 
`de.monticore.cd`.
MontiCore projects are hosted at

* [`https://git.rwth-aachen.de/monticore`](https://git.rwth-aachen.de/monticore), 
    and partially also at
* [`https://github.com/MontiCore/`](https://github.com/MontiCore/monticore)


## List of Languages 

<!--
### [Activity Diagrams](INSERT LINK HERE) (not adressed yet)
* TO be added
-->


### [Class Diagram For Analysis (CD4A)](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis) (Beta: In Stabilization)
* Responsible: SVa, AGe
* CD4A is the textual representation to describe UML class diagrams 
  (it uses the [UML/P](http://mbse.se-rwth.de/) variant).
* CD4A covers **classes, interfaces, inheritance, attributes with types,
  visibilities**,
  and all kinds of **associations** and **composition**, including **qualified**
  and **ordered associations**. 
* It focusses on the analysis phase in typical data-driven development 
  projects and is therefore mainly for data modelling.
  Consequently, it omits method signatures and complex generics.
  CD4A primary use is therefore **data modelling**. It has various 
  possibilities for generation of data structures, database tables as well as 
  data transport infrastructures in cloud and distributed systems.
* [Main grammar `de.monticore.cd.CD4Analysis`](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/grammars/de/monticore/cd/CD4Analysis.mc4)
  and 
  [*detailed description*](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/blob/develop/cd4analysis.md)
<!-- Status: ok, BR 20.03.22 -->


### [Class Diagram for Code (CD4Code)](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis) (Beta: In Stabilization)
* Responsible: SVa, AGe
* CD4Code describes **UML class diagrams**.
* CD4Code is a conservative extension of **CD4A**, 
  which includes method signatures.
* CD4Code is often used as tool-internal intermediate AST that allows to
  map any kind of source models to a class/attribute/method/association based
  intermediate structure, before it is printed e.g. as Java code. 
  A typical path is e.g. Statechart -> State pattern encoded in CD4Code 
  -> Decoration by monitoring methods -> Java code.
* Main grammar [`de.monticore.cd.CD4Code`](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/grammars/de/monticore/cd/CD4Code.mc4)
  and 
  [*detailed description*](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/blob/develop/cd4analysis.md) 
  (see Section *CD4Code*)
<!-- Status: ok, BR 20.03.22 -->


### [Feature Diagrams](https://git.rwth-aachen.de/monticore/languages/feature-diagram) (Beta: In Stabilization)
* Caretaker: AB, DS
* Language for textual feature models and feature configurations
* Feature diagrams are used to model (software) product lines
* Feature configurations select a subset of features of a feature model 
  to describe a product of the product line
<!-- Status: TODO: Teaser Erstellen, siehe CD4A -->


### [GUI DSL](https://git.rwth-aachen.de/macoco/gui-dsl) (Alpha: Intention to become stable)
* Caretaker: LN 
* Language for textual definition of Graphical User Interfaces of Web Applications
* Examples: [**MaCoCo**](https://git.rwth-aachen.de/macoco/implementation), 
       [**Ford**](https://git.rwth-aachen.de/ford/implementation/frontend/montigem)
* Documentation: [**here**](https://git.rwth-aachen.de/macoco/gui-dsl/wikis/home)
<!-- Status: TODO: Teaser Erstellen, siehe CD4A -->


### [MontiCore Grammar](https://git.rwth-aachen.de/monticore/monticore/blob/dev/monticore-generator) (MontiCore Stable)
* Caretaker: MB 
* Language for MontiCore Grammars itself. It can be understood as 
  *meta language*, but also used as ordinary language.
* Its main use currently: A MontiCore grammar defines the 
  **concrete syntax** and the **abstract syntax** of a textual language.
  Examples: All languages on this page are defined using MontiCore grammars
  and thus conform to this Grammar.
* Main features: Define **nonterminals** and their **productions** in EBNF, 
  **lexical token** as regular expressions. 
* Extensions:
  * **Abstract**, **interface** and **external productions** allow to
    define extensible component grammars (object-oriented grammar style).
  * Inherited productions can be redefined (overwritten) as well
    as conservatively extended.
  * **Symbols definition** places can be introduced and 
    **symbol referencing places** defined, such that for standard cases
    automatically symbol tables can be added.
  * Additional attributes and methods can be added to the abstract syntax only.
  * Various elements, such as **semantic predicates** and **actions**
    can be defined in the same style as the underlying ANTLR.
* Main grammars 
  [`de.monticore.grammar.Grammar`](https://git.rwth-aachen.de/monticore/monticore/-/blob/dev/monticore-generator/src/main/grammars/de/monticore/grammar/Grammar.mc4)
  defines the language with some open parameters and
  [`de.monticore.grammar.Grammar_WithConcepts`](https://git.rwth-aachen.de/monticore/monticore/-/blob/dev/monticore-generator/src/main/grammars/de/monticore/grammar/Grammar_WithConcepts.mc4)
  binds the external, imported expressions, method bodies, etc.
* [*Detailed description*](http://monticore.de/MontiCore_Reference-Manual.2017.pdf)
  in the MontiCore Reference Manual.
<!-- Status: ok, BR 20.03.22 -->
  

### [JSON](https://git.rwth-aachen.de/monticore/languages/json) (Beta: In Stabilization)
* Caretaker: NJ
* MontiCore language for parsing JSON artifacts.
<!-- Status: TODO: Teaser Erstellen, siehe CD4A -->


### [MontiArc](https://git.rwth-aachen.de/monticore/montiarc/core) (Beta: In Stabilization)
* Caretaker: DS 
* MontiArc is an architecture and behavior modeling language and framework 
    that provides an integrated, platform independent structure and behavior 
    modeling language with an extensible code generation framework.
* [Port Automata](https://git.rwth-aachen.de/monticore/montiarc/core) 
    are a certain type of state machines and utilized in component and 
    connector architecture description languages (e.g. MontiArc) for 
    behavior modeling. (Alpha: Intention to become stable)
<!-- Status: TODO: Teaser Erstellen, siehe CD4A -->


### [OCL/P](https://git.rwth-aachen.de/monticore/languages/OCL) (Alpha: Intention to become stable)
* Caretaker: SVa
* OCL/P is the textual representation of the UML OCL standard, adapted 
  with Java-like syntax.
  It's main goal is the usage in combination with other languages like 
  CD4A or Object Diagrams as an integrated part of that languages.
* OCL/P allows to define **invariants** and **pre/post conditions** in 
  the known OCL style. Furthermore, it offers a large set **expressions**
  to model constraints. These expressions include **Java expressions**,
  **set operations**, **list operations** etc., completely covering the 
  OCL standard concepts, but extend it e.g. by **set comprehensions** 
  known from Haskell, a **typesafe cast** or a 
  **transitive closure operator**.
* OCL/P comes with an 
  [OCL to Java generator](https://git.rwth-aachen.de/monticore/languages/OCL2Java)
  and a second generator for OCL in combination with 
  [*Embedded MontiArc*](https://git.rwth-aachen.de/monticore/EmbeddedMontiArc/generators/OCL_EMA2Java).
* [Main grammar `ocl.monticoreocl.OCL`](https://git.rwth-aachen.de/monticore/languages/OCL/-/blob/master/src/main/grammars/ocl/monticoreocl/OCL.mc4)
  and 
  [*detailed description*](https://git.rwth-aachen.de/monticore/languages/OCL/-/blob/master/OCL.md)
<!-- Status: ok, BR 20.03.22 -->


### [Object Diagrams](https://git.rwth-aachen.de/monticore/languages/od) (Beta: In Stabilization)
* Caretaker: SH
* Language for textual object diagrams.
<!-- Status: TODO: Teaser Erstellen, siehe CD4A -->


### [Sequence Diagrams](https://git.rwth-aachen.de/monticore/statechart/sd-language)  (Beta: In Stabilization) )(50% to MC6)
* Caretaker: RE
* Grammar to parse Sequence Diagrams
* Can be used with testing generator to derive test cases
<!-- Status: TODO: Teaser Erstellen, siehe CD4A -->


### [SI Units](https://git.rwth-aachen.de/monticore/languages/siunits) (Alpha: Intention to become stable)
* Caretaker: EK, NJ, DS
* allows a language developer to use physical units in a language
<!-- Status: TODO: Teaser Erstellen, siehe CD4A -->


### [Statecharts](https://git.rwth-aachen.de/monticore/statechart/sc-language) (Beta: In Stabilization) (90% to MC6)
* Caretaker: RE supported by KH with two Hiwis 
* Language to parse Statecharts
* creates transformation language within SC and sc<->cd4a
<!-- Status: TODO: Teaser Erstellen, siehe CD4A -->


### [SysML/P](https://git.rwth-aachen.de/monticore/sysml/sysml_2) (Alpha: Intention to become stable)
* Caretaker: NJ
* Project for SysML 2 languages. It is compatible with the general SysML 2 standard.
<!-- Status: TODO: Teaser Erstellen, siehe CD4A -->


### [Tagging](https://git.rwth-aachen.de/monticore/EmbeddedMontiArc/languages/Tagging) (Alpha: Intention to become stable)
* Caretaker: SVa
* **Tags** are known e.g. from the UML and SysML and mainly used to add
  extra information to a model element. 
  Normally tags (and **stereotypes**) are inserted within the models,
  which over time polutes the models, especially when different sets of
  tags are needed for different technical platforms.
* MontiCore offers a solution that **separates a model and its tags into
  distinct artifacts**. Several independent tagging artifacts
  can be added without any need to adapt the core model.
  This allows fo reuse even of fixed library models.
* The tagging artifacts are dependent on two factors:
  * First, **tags** can be added to named elements of the base model.
    It is of great help that we have an elegant symbol mechanism included 
    in the MontiCore generator.
  * Second, the set of allowed tags can be constrained, a by an explicit
    definition of allowed **tag types** and **tag values** and an explicit 
    declaration on which **kinds of symbols** a tag may be attached to.
  Consequently tagging is not a single language, but a method to 
  **automatically and schematicall derive** languages:
  # A tagging schema language TSL (dependent on the available symbol types
    of the base grammar)
  # a tagging language TL (dependent on the tag schema models written in TSL)
* Because tagging models can e.g. be used as configuration techniques 
  in a code generator, appropriate infrastructure is generated as well.
* Some [**tagging language examples**](https://git.rwth-aachen.de/monticore/EmbeddedMontiArc/languages/Tagging-Examples)
* Although concrete languages (and their grammars) are themselves generated,
  there is a 
  [main grammar `ocl.monticore.lang.Tagging`](https://git.rwth-aachen.de/monticore/EmbeddedMontiArc/languages/Tagging/-/blob/master/src/main/grammars/de/monticore/lang/Tagging.mc4),
  where the tagging langue is derived from.
  See also [*detailed description*](https://git.rwth-aachen.de/monticore/EmbeddedMontiArc/languages/Tagging/-/blob/master/Tagging.md)
<!-- Status: TODO SV: Check 20.03.23 -->


### [XML](https://git.rwth-aachen.de/monticore/languages/xml) (Alpha: Intention to become stable)
* Caretaker: NJ
* MontiCore language for parsing XML artifacts.
<!-- Status: TODO: Teaser Erstellen, siehe CD4A -->


### [JavaLight](https://git.rwth-aachen.de/monticore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/JavaLight.mc4) (Beta: In Stabilization)
* Caretaker: MB
* This is a reduced version of the **Java language**.
  JavaLight is meant to be used to integrate simplified Java-like parts 
  in modeling languages but not to parse complete Java implementations.
* It provides Java's **attribute** and **method definitions**, 
  **statements** and **expressions**, but 
  does not provide class or interface definitions and
  also no wildcards in the type system.
* One main usage of JavaLight is in the Grammar-language to model e.g. 
  Java methods. 
* [Main grammar `de.monticore.JavaLight`]((https://git.rwth-aachen.de/monticore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/JavaLight.mc4)
  and 
  [*detailed description*](to Be Defined).
<!-- Status: TODO: Detailed description erstellen und verlinken-->



### [Java](https://git.rwth-aachen.de/monticore/javaDSL) (Beta: In Stabilization) (30% to MC6)
* Caretaker: MB
* This is the full Java' Language (as Opposed to JavaLight).
<!-- Status: TODO: Teaser Erstellen, siehe CD4A -->


## Further Information

* see also [**MontiCore Reference Manual**](http://www.monticore.de/)

* [MontiCore project](../README.md) - MontiCore

