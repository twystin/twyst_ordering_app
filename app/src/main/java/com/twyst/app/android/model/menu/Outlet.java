package com.twyst.app.android.model.menu;

import java.io.Serializable;

/**
 * Created by Vipul Sharma on 11/24/2015.
 */
public class Outlet implements Serializable {
    private String _id;

    private String name;

    private String loc1;

    private String loc2;

    public String get_id ()
    {
        return _id;
    }

    public void set_id (String _id)
    {
        this._id = _id;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getLoc1 ()
    {
        return loc1;
    }

    public void setLoc1 (String loc1)
    {
        this.loc1 = loc1;
    }

    public String getLoc2 ()
    {
        return loc2;
    }

    public void setLoc2 (String loc2)
    {
        this.loc2 = loc2;
    }

}
