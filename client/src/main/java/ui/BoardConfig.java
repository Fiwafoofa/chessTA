package ui;

public class BoardConfig {

  private String edgeBGColor = EscSeq.SET_BG_COLOR_DARK_GREY;
  private String edgeTextColor = EscSeq.SET_TEXT_COLOR_WHITE;
  private String reset = EscSeq.RESET_BG_COLOR + EscSeq.RESET_TEXT_COLOR;
  private String whiteSquareBGColor = EscSeq.SET_BG_COLOR_WHITE;
  private String whiteTextColor = EscSeq.SET_TEXT_COLOR_BLUE;
  private String blackSquareBGColor = EscSeq.SET_BG_COLOR_BLACK;
  private String blackTextColor = EscSeq.SET_TEXT_COLOR_RED;
  private String highlightBlackSquareBGColor = EscSeq.SET_BG_COLOR_DARK_GREEN;
  private String highlightWhiteSquareBGColor = EscSeq.SET_BG_COLOR_GREEN;
  private String highlightPositionBGColor = EscSeq.SET_BG_COLOR_YELLOW;

  public String getEdgeTextColor() {
    return edgeTextColor;
  }

  public void setEdgeTextColor(String edgeTextColor) {
    this.edgeTextColor = edgeTextColor;
  }

  public String getReset() {
    return reset;
  }

  public void setReset(String reset) {
    this.reset = reset;
  }

  public String getWhiteSquareBGColor() {
    return whiteSquareBGColor;
  }

  public void setWhiteSquareBGColor(String whiteSquareBGColor) {
    this.whiteSquareBGColor = whiteSquareBGColor;
  }

  public String getWhiteTextColor() {
    return whiteTextColor;
  }

  public void setWhiteTextColor(String whiteTextColor) {
    this.whiteTextColor = whiteTextColor;
  }

  public String getBlackSquareBGColor() {
    return blackSquareBGColor;
  }

  public void setBlackSquareBGColor(String blackSquareBGColor) {
    this.blackSquareBGColor = blackSquareBGColor;
  }

  public String getBlackTextColor() {
    return blackTextColor;
  }

  public void setBlackTextColor(String blackTextColor) {
    this.blackTextColor = blackTextColor;
  }

  public String getEdgeBGColor() {
    return edgeBGColor;
  }

  public void setEdgeBGColor(String edgeBGColor) {
    this.edgeBGColor = edgeBGColor;
  }

  public String getHighlightBlackSquareBGColor() {
    return highlightBlackSquareBGColor;
  }

  public void setHighlightBlackSquareBGColor(String highlightBlackSquareBGColor) {
    this.highlightBlackSquareBGColor = highlightBlackSquareBGColor;
  }

  public String getHighlightWhiteSquareBGColor() {
    return highlightWhiteSquareBGColor;
  }

  public void setHighlightWhiteSquareBGColor(String highlightWhiteSquareBGColor) {
    this.highlightWhiteSquareBGColor = highlightWhiteSquareBGColor;
  }

  public String getHighlightPositionBGColor() {
    return highlightPositionBGColor;
  }
}
