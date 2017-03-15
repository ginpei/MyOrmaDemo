package info.ginpei.myormademo.basicModels;

import com.github.gfx.android.orma.annotation.Column;
import com.github.gfx.android.orma.annotation.PrimaryKey;
import com.github.gfx.android.orma.annotation.Table;

@Table
public class User {
    @PrimaryKey(autoincrement = true)
    public long id;

    @Column(defaultExpr = "")
    public String name;
}
