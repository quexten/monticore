<#-- (c) https://github.com/MontiCore/monticore */ -->
${tc.signature("scopeDeSerName", "grammarName")}
  this.modelPath = new de.monticore.io.paths.ModelPath();
  this.fileExt = "";
  this.scopeDeSer = new ${scopeDeSerName}();
  this.symbols2Json = new ${symbols2Json}();
  init();