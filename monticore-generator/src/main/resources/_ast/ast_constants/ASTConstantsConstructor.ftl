<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("superGrammarList")}
superGrammars = new String[] {<#list superGrammarList as grammar>"${grammar}"<#if !grammar?is_last>,</#if></#list>};
