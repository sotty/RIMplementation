RIMplementation
===============

An OWL implementation of the HL7 RIM ontology
Folders:

- RIM-Model
A Maven project which derives the RIM Ontology from the HL7 MIF files,
along with other artifacts such as XSD schemas

- owl
A snapshot of the current ontology. It is a modular OWL ontology, with
dependencies as follows:

rim_all.owl
 - rim-core.owl
 - - rim-upper.owl
 - - - datatype.owl
 - rim_voc.owl
 - - rim-upper-voc.owl
 - - - edu/mayo/cts2/terms/terms.owl
 - - - - edu/mayo/cts2/terms/cts2.owl
 - - - - edu/mayo/cts2/terms/lmm_l1.owl
 - - - - edu/mayo/cts2/terms/skos.owl