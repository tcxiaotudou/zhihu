package cn.fciasth.zhihu.vo;

import java.util.HashMap;
import java.util.Map;

public class ViewObject {

    private Map<String,Object> vo = new HashMap<String, Object>();

    public void set(String key,Object value){
        vo.put(key,value);
    }

    public Object get(String key){
        return vo.get(key);
    }
}
