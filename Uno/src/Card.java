public class Card{

  public enum Action {Skip, Draw2, Reverse};
  public enum Wild {Wild, WildDraw4};
  public enum Colour {Blue, Green, Red, Yellow};

  private Colour colour;
  private Wild wild;
  private Action action;
  private Integer number;
  private Integer value;

  public Card(Colour colour, int number){
    this.colour = colour;
    this.number = number;
    this.value = number;
  }
  

  public Card(Colour colour, Action action){
    this.colour = colour;
    this.action = action;
    this.value = 20;
  }

  public Card(Wild wild){
    this.wild = wild;
    this.value = 50;
  }

  public String getColour(){
    if(this.colour != null){
      return this.colour.name();
    }
    return "black";
  }

  public int getNumber(){
    if(this.number != null){
      return this.number;
    }
    return -10;
  }

  public String getAction(){
    if(this.action != null){
      return this.action.name();
    }
    return "none";
  }

  public String getWild(){
    if(this.wild != null){
      return this.wild.name();
    }
    return null;
  }
  
  public int getValue() {
	  return value;
  }

  public boolean match(Card card){
    return this.getColour() == card.getColour() || this.getNumber() == card.getNumber() || 
    	   this.getAction() == card.getAction() && !this.getAction().equals(null) || this.getColour() == "black";
  }
  
  @Override
  public boolean equals(Object object){
	    Card card = (Card) object;
	    return this.getColour() == card.getColour() || this.getNumber() == card.getNumber() || this.getColour() == "black" &&
	    		this.getWild() == card.getWild()|| this.getColour() == card.getColour() && this.getAction() == card.getAction();
  }

  @Override
  public int hashCode() {
	  if(this.getWild() != null) {
		  return Wild.valueOf(this.getWild()).ordinal() * -10;
	  }
	  if(this.getAction() != "none") {
		  return Action.valueOf(this.getAction()).ordinal() * 10 + Colour.valueOf(this.getColour()).ordinal(); 
	  }
      return this.getNumber() * 5 + Colour.valueOf(this.getColour()).ordinal();
  }
  
  public String toString(){
    if(this.getWild() != null){
      return this.getWild();
    }
    if(this.getAction() == "none"){
      return this.getColour() + this.getNumber();
    }
    return this.getColour() + this.getAction();
  }
}