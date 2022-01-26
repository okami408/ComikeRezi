class Item { 
    private int id;//管理番号
    private String name;//商品名
    private int price;//商品価格(単価)
    private int zaiko;//在庫数
    private int count = 0;//販売数
//    private int firstZaiko;//初期在庫数

    public Item(int id,String name, int price, int zaiko){
        this.id = id;
        this.name = name;
        this.price = price;
        this.zaiko = zaiko;
    }
//ゲッターここから
    //管理番号
    public int getId(){
        return id;
    }
    //商品名
    public String getName(){
        return name;
    }
    //価格
    public int getPrice(){
        return price;
    }
    //在庫数
    public int getZaiko(){
        return zaiko;
    }
    //販売数
    public int getCount(){
        return count;
    }
//ゲッターここまで
//セッターここから
    //在庫数
    public void setZaiko(int zaiko){
        this.zaiko += zaiko;
    }
    //販売数
    public void setCount(int count){
        this.count += count;
    }

    //表示
    @Override
    public String toString(){
        return "管理番号:"+ this.getId() +",商品名:"+ this.getName() +",価格:"+ this.getPrice() +",在庫:"+ this.getZaiko() +",販売数:" + this.getCount();
    }

}