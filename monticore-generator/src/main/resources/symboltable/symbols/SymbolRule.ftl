<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("ruleSymbol")}
<#assign genHelper = glex.getGlobalVar("stHelper")>

<#list ruleSymbol.getAdditionalAttributeList() as attr>
  <#assign attrName = "_" + attr.getName()>
  <#assign attrType=genHelper.getQualifiedASTName(attr.getMCType().getBaseName())>
  <#if attr.isPresentCard()>
    <#if attr.getCard().isPresentMax()>
      <#if attr.getCard().getMax() == "*">
        <#assign attrType = "java.util.List<" + attrType + ">">
      <#elseif attr.getCard().isPresentMin() && attr.getCard().getMin() == "0">
        <#assign attrType = "Optional<" + attrType + ">">
      </#if>
    <#elseif attr.getCard().isPresentMin() && attr.getCard().getMin() == "0">
      <#assign attrType = "Optional<" + attrType + ">">
    </#if>
  </#if>

  private ${attrType} ${attrName};

  <#if attrType == "boolean" || attrType == "Boolean">
    <#if attr.getName()?starts_with("is")>
      <#assign methodName=attr.getName()>
    <#else>
      <#assign methodName="is" + attr.getName()?cap_first>
    </#if>
  <#else>
    <#assign methodName="get" + attr.getName()?cap_first>
  </#if>
  public ${attrType} ${methodName}() {
    return this.${attrName};
  }
  
  public void set${attr.getName()?cap_first}(${attrType} ${attrName}) {
    this.${attrName} = ${attrName};
  }
  
</#list>

<#list ruleSymbol.getMethodList() as meth>
  ${genHelper.printMethod(meth)}
</#list>