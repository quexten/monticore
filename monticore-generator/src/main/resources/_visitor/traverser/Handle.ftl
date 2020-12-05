<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("handler", "doTraverse")}
  if (get${handler}().isPresent()) {
    get${handler}().get().handle(node);
  } else {
    visit(node);
<#if doTraverse>
    traverse(node);
<#else>
    // no traverse() for abstract classes, interfaces and enums, only concrete classes are traversed
</#if>
    endVisit(node);
  }