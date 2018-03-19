/* (c)  https://github.com/MontiCore/monticore */
package de.monticore.ast;

import de.se_rwth.commons.SourcePosition;

/**
 * Builder for RTE class {@link Comment}
 */
public class CommentBuilder {
  
  protected String text;
  
  protected SourcePosition start = SourcePosition.getDefaultSourcePosition();
  
  protected SourcePosition end = SourcePosition.getDefaultSourcePosition();
  
  public Comment build() {
    Comment res = new Comment(text);
    res.set_SourcePositionStart(start);
    res.set_SourcePositionEnd(end);
    return res;
  }
  
  public SourcePosition get_SourcePositionEnd() {
    return end;
  }
  
  public CommentBuilder set_SourcePositionEnd(SourcePosition end) {
    this.end = end;
    return this;
  }
  
  public SourcePosition get_SourcePositionStart() {
    return start;
  }
  
  public CommentBuilder set_SourcePositionStart(SourcePosition start) {
    this.start = start;
    return this;
  }
  
  public String getText() {
    return this.text;
  }
  
  public CommentBuilder setText(String text) {
    this.text = text;
    return this;
  }
}