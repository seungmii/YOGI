package yogi.lendplace;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import yogi.common.dao.AbstractDAO;

@Repository("lendplaceDAO")
public class LendplaceDAO extends AbstractDAO{

//	장소목록 조회 
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectLendplaceList(Map<String, Object> map) throws Exception{
		return (List<Map<String, Object>>)selectList("lendplace.selectLendplaceList",map);
	}

//	장소상세페이지 조회
	@SuppressWarnings("unchecked")
	public Map<String, Object> selectLendplaceDetail(Map<String, Object> map) throws Exception{
		return (Map<String, Object>) selectOne("lendplace.selectLendplaceDetail",map);
	}
	
//	날짜 중복체크
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> dateCheck(Map<String, Object> map) throws Exception{
		return (List<Map<String, Object>>) selectList("lendplace.dateCheck",map);
	}

//	장소 내용 수정 
	public void updateLendplace(Map<String, Object> map) throws Exception{
		update("lendplace.updateLendplace",map);
	}
	
	
//	장소후기 입력 
	public void insertReview(Map<String, Object> map) throws Exception{
		insert("lendplace.insertReview",map);
	}
	
//	장소후기 삭제
	public void deleteReview(Map<String, Object> map) throws Exception{
		delete("lendplace.deleteReview",map);
	}

}
