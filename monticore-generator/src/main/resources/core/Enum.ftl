<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdEnum")}
/* (c) https://github.com/MontiCore/monticore */
${tc.include("core.Package")}

${tc.include("core.Annotations")}
public enum ${cdEnum.getName()}<#if !cdEnum.isEmptyInterface()> implements ${cdEnum.printInterfaces()}</#if> {

<#list cdEnum.getCDEnumConstantList() as constants>
  ${tc.include("core.EmptyConstants", constants)}<#if !constants?is_last>,</#if>
</#list>
;

<#list cdEnum.getCDAttributeList() as attribute>
  ${tc.include("core.Attribute", attribute)}
</#list>

<#list cdEnum.getCDConstructorList() as constructor>
  ${tc.include("core.Constructor", constructor)}
</#list>

<#list cdEnum.getCDMethodList() as method>
  ${tc.include("core.Method", method)}
</#list>
}
