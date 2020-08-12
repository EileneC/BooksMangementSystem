package DatabasesOperation.DAO_Design.DAOImpl;

import DatabasesOperation.DAO_Design.ORM.ORM_Reader;
import DatabasesOperation.JDBCUtils.JDBCUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对读者表的ORM类进行CRUD操作
 */

public class DAOReader {
    private NamedParameterJdbcTemplate nJDBC;

    public DAOReader() {
        this.nJDBC = new NamedParameterJdbcTemplate(JDBCUtils.getDatasource());
    }

    //查询自己的信息,因为reader不会重复的，所以只返回一个。
    public ORM_Reader read(ORM_Reader reader) {//根據id
        String sql = "select * from reader where id=:id";
        Map<String, Integer> params = new HashMap<>();
        params.put("id", reader.getId());
        List<ORM_Reader> list = nJDBC.query(sql, params, new BeanPropertyRowMapper<>(ORM_Reader.class));
        if (list.size() != 0)
            return list.get(0);//返回一个
        else return null;
    }

    //查询自己的信息,因为reader不会重复的，所以只返回一个。
    public ORM_Reader readReader(String readerName) {//根據名字
        String sql = "select * from reader where name=:name";
        Map<String, String> params = new HashMap<>();
        params.put("name", readerName);
        List<ORM_Reader> list = nJDBC.query(sql, params, new BeanPropertyRowMapper<>(ORM_Reader.class));
        if (list.size() != 0)
            return list.get(0);//返回一个
        else return null;
    }

    /**
     * 删除一个reader，当该reader还没有把书全都还就不允许删除
     *
     * @param reader 要删除的reader
     */
    public void deleteReader(ORM_Reader reader) {//刪除读者，限制只有它借的书都还了才能删除
        Integer[] res = new DAOBorrow().findMyBorrow(reader);//查询是否有借书
        if (res==null||res.length == 0) {
            delete(reader, true);
        } else
            System.out.println(reader.getName() + "还有" + res.length + "本书没还，无法删除！");//修改為窗

        //可以给个提示，是否需要强制删除？是则强行删除
        //delete(reader,true);

    }

    /**
     * 是否删除该reader，
     *
     * @param reader 要删除的reader
     * @param b      选择强制删除吗
     */

    private void delete(ORM_Reader reader, boolean b) {
        if (b) {
            String sql = "delete from reader where id=:id and name=:name";
            Map<String, Object> params = new HashMap<>();
            params.put("name", reader.getName());
            params.put("id", reader.getId());
            nJDBC.update(sql, params);
        }
    }

    /**
     * 插入一个reader
     *
     * @param reader 要插入的reader
     *
     * @return 返回布尔值表示插入的结果
     */
    public boolean addReader(ORM_Reader reader) {
        String sql = "insert into reader(id,name,borrow_day,borrow_num,sex) values(:id,:name,:borrow_day,:borrow_num,:sex)";
        SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(reader);
        int i =nJDBC.update(sql, sqlParameterSource);
        if(i==0)
            return false;
        return true;//添加完成
    }



}