/* generated from model ItTestGrammar */
/* generated by template core.Class*/

/* (c) https://github.com/MontiCore/monticore */
package mc.grammar.ittestgrammar._ast;

import de.se_rwth.commons.Names;

 public   class ASTGenericType extends ASTGenericTypeTOP {

 public  String toString ()  {
      return printGenericType(this);
}

 public  String getTypeName ()  {
      return printGenericType(this);
}

 public  boolean isExternal ()  {
      return true;
}
  
   protected String printGenericType(ASTGenericType genericType) {
    
     StringBuilder b = new StringBuilder();
    
     b.append(Names.getQualifiedName(genericType.getNameList()));
    
     boolean first = true;
     for (ASTGenericType t : genericType.getGenericTypeList()) {
       if (first) {
         b.append("<");
         first = false;
       }
       else {
         b.append(",");
        
       }
      
       b.append(printGenericType(t));
     }
    
     if (!first) {
       b.append(">");
     }
    
     int dimension = genericType.getDimension();
     for (int i = dimension; i > 0; i--) {
       b.append("[]");
     }
    
     return b.toString();
   }

}
