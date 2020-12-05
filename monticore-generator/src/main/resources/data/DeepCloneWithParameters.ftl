<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeList")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
<#assign service = glex.getGlobalVar("service")>
    super.deepClone(result);

<#list attributeList as attribute>
<#if attribute.isPresentModifier() && !service.isReferencedSymbolAttribute(attribute) && !service.isInheritedAttribute(attribute)>
    <#assign attributeName = attribute.getName()>
    <#assign methName = genHelper.getNativeAttributeName(attribute.getName())?cap_first>
    <#assign attrType = attribute.getMCType()>
    <#assign typeName = genHelper.printType(attribute.getMCType())>
    <#if genHelper.isSimpleAstNode(attribute)>
      result.set${methName}(get${methName}().deepClone());
    <#elseif genHelper.isPrimitive(attrType)>
      result.set${methName}(${genHelper.getPlainGetter(attribute)}());
    <#elseif genHelper.isOptional(attribute.getMCType())>
        <#assign reference = genHelper.getReferenceTypeFromOptional(attrType)>
    <#assign referenceName = typeName>
    <#if genHelper.isString(reference) || genHelper.isAttributeOfTypeEnum(attribute)>
    if (isPresent${methName}()) {
      result.set${methName}(get${methName}());
    } else {
      result.set${methName}Absent();
    }
    <#elseif genHelper.isOptionalAstNode(attribute)>
    if (isPresent${methName}()){
      result.set${methName}(get${methName}().deepClone());
    } else {
      result.set${methName}Absent();
    }
    <#elseif !service.isReferencedSymbolAttribute(attribute)>
    if (isPresent${methName}()){
      result.set${methName}(get${methName}());
    } else {
      result.set${methName}Absent();
    }
    </#if>
  <#else>
     <#assign getter =genHelper.getPlainGetter(attribute)>
    <#if genHelper.isListAstNode(attribute)>
      ${getter}().forEach(s -> result.${attributeName}.add(s.deepClone()));
    <#elseif genHelper.isListType(typeName)>
      for ( int i = 0; i < this.${getter}().size() ; i++ ) {
        result.add${getter?remove_ending("List")?remove_beginning("get")}(${getter?remove_ending("List")}(i));
      }
    <#else>
      result.set${methName}(get${methName}());
    </#if>
  </#if>
</#if>
</#list>
    
    return result;
