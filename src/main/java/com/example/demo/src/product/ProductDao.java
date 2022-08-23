package com.example.demo.src.product;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.product.model.GetCategoryDepth01Res;
import com.example.demo.src.product.model.GetCategoryDepth02Res;
import com.example.demo.src.product.model.GetCategoryDepth03Res;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Queue;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Repository //  [Persistence Layer에서 DAO를 명시하기 위해 사용]

/**
 * DAO란?
 * 데이터베이스 관련 작업을 전담하는 클래스
 * 데이터베이스에 연결하여, 입력 , 수정, 삭제, 조회 등의 작업을 수행
 */
public class ProductDao {

    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************

    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    // ******************************************************************************

    /**
     * DAO관련 함수코드의 전반부는 크게 String ~~~Query와 Object[] ~~~~Params, jdbcTemplate함수로 구성되어 있습니다.(보통은 동적 쿼리문이지만, 동적쿼리가 아닐 경우, Params부분은 없어도 됩니다.)
     * Query부분은 DB에 SQL요청을 할 쿼리문을 의미하는데, 대부분의 경우 동적 쿼리(실행할 때 값이 주입되어야 하는 쿼리) 형태입니다.
     * 그래서 Query의 동적 쿼리에 입력되어야 할 값들이 필요한데 그것이 Params부분입니다.
     * Params부분은 클라이언트의 요청에서 제공하는 정보(~~~~Req.java에 있는 정보)로 부터 getXXX를 통해 값을 가져옵니다. ex) getEmail -> email값을 가져옵니다.
     *      Notice! get과 get의 대상은 카멜케이스로 작성됩니다. ex) item -> getItem, password -> getPassword, email -> getEmail, userIdx -> getUserIdx
     * 그 다음 GET, POST, PATCH 메소드에 따라 jabcTemplate의 적절한 함수(queryForObject, query, update)를 실행시킵니다(DB요청이 일어납니다.).
     *      Notice!
     *      POST, PATCH의 경우 jdbcTemplate.update
     *      GET은 대상이 하나일 경우 jdbcTemplate.queryForObject, 대상이 복수일 경우, jdbcTemplate.query 함수를 사용합니다.
     * jdbcTeplate이 실행시킬 때 Query 부분과 Params 부분은 대응(값을 주입)시켜서 DB에 요청합니다.
     * <p>
     * 정리하자면 < 동적 쿼리문 설정(Query) -> 주입될 값 설정(Params) -> jdbcTemplate함수(Query, Params)를 통해 Query, Params를 대응시켜 DB에 요청 > 입니다.
     * <p>
     * <p>
     * DAO관련 함수코드의 후반부는 전반부 코드를 실행시킨 후 어떤 결과값을 반환(return)할 것인지를 결정합니다.
     * 어떠한 값을 반환할 것인지 정의한 후, return문에 전달하면 됩니다.
     * ex) return this.jdbcTemplate.query( ~~~~ ) -> ~~~~쿼리문을 통해 얻은 결과를 반환합니다.
     */

    /**
     * 참고 링크
     * https://jaehoney.tistory.com/34 -> JdbcTemplate 관련 함수에 대한 설명
     * https://velog.io/@seculoper235/RowMapper%EC%97%90-%EB%8C%80%ED%95%B4 -> RowMapper에 대한 설명
     */

    /**
     * 카테고리 항목 조회
     */
    public List<GetCategoryDepth01Res> getCategoryDepth01() {
        String Query = "SELECT id, name FROM Category WHERE status='active'";
        return this.jdbcTemplate.query(Query,
                (rs, rn) -> new GetCategoryDepth01Res(
                        rs.getInt("id"),
                        rs.getString("name"),
                        false
                ));
    }

    /**
     * 카테고리 항목 조회 - 세부 카테고리 1
     */
    public List<GetCategoryDepth02Res> getCategoryDepth02(int depth1Id) {
        String Query = "SELECT id,categoryId, name FROM CategoryDepth2 WHERE status='active' AND categoryId = ?";
        return this.jdbcTemplate.query(Query,
                (rs, rn) -> new GetCategoryDepth02Res(
                        rs.getInt("id"),
                        rs.getString("name"),
                        false
                ),
                depth1Id);
    }

    /**
     * 카테고리 항목 조회 - 세부 카테고리 2
     */
    public List<GetCategoryDepth03Res> getCategoryDepth03(int depth2Id) {
        String Query = "SELECT id,category2Id, name FROM CategoryDepth3 WHERE status='active' AND category2Id = ?";
        return this.jdbcTemplate.query(Query,
                (rs, rn) -> new GetCategoryDepth03Res(
                        rs.getInt("id"),
                        rs.getString("name"),
                        false
                ),
                depth2Id);
    }


    /**
     * 카테고리 항목 조회 - 세부 카테고리 1
     */
    public void getMatchCategory(int depth1Id, int depth2Id) {
        String Query = "SELECT id,categoryId, name FROM CategoryDepth2 WHERE status='active' AND categoryId = ? And id=?";
        this.jdbcTemplate.queryForObject(Query,
                (rs, rn) -> rs.getInt("id"),
                depth1Id, depth2Id);
    }
}
