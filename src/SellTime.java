class SellTime {
    private String zikan;//売り上げた日時
    private String name;//販売商品名

    public SellTime(String zikan, String name){
        this.zikan = zikan;
        this.name = name;
    }

    public String toString(){
        return zikan + " " + name;
    }
}