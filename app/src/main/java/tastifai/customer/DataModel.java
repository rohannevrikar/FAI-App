package tastifai.customer;

/**
 * Created by Rohan Nevrikar on 18-02-2018.
 */

public class DataModel
{
    private String name;
    private int resId;

    public DataModel(String name, int resId)
    {
        this.name = name;
        this.resId = resId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getResId()
    {
        return resId;
    }

    public void setResId(int resId)
    {
        this.resId = resId;
    }
}