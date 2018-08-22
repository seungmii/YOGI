package yogi.group;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import yogi.group.GroupService;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import yogi.common.common.YogiConstants;
import yogi.common.util.PagingCalculator;
import yogi.common.util.YogiUtils;
import yogi.config.CommandMap;

@Controller
public class GroupController {
	Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	@Resource(name="groupService")
	private GroupService groupService;
	
	@Resource(name="alramService")
	private AlramService alramService;
	
	@Resource(name="groupDAO")
	private GroupDAO groupDAO;
	
	@RequestMapping(value="/groupList", method={RequestMethod.GET, RequestMethod.POST})
    public ModelAndView groupList(CommandMap map, HttpServletRequest request) throws Exception{
		YogiUtils.savePageURI(request);
		ModelAndView mv = new ModelAndView("/group/groupList");
		List<Map<String,Object>> list = groupService.selectGroupList(map.getMap());
		PagingCalculator paging = new PagingCalculator("group/groupList", map.getCurrentPageNo(), list, 6 ,3);
		Map<String, Object> result = paging.getPagingList();
		mv.addObject("list", result.get("list"));
		mv.addObject("pagingHtml", result.get("pagingHtml"));
		mv.addObject("currentPageNo", map.getCurrentPageNo());
        return mv;
    }	
	
	@RequestMapping(value="/groupDetail", method={RequestMethod.POST, RequestMethod.GET})
	public ModelAndView groupDetail(CommandMap map, HttpServletRequest request) throws Exception{
		YogiUtils.savePageURI(request);
		ModelAndView mv = new ModelAndView("/group/groupDetail");
		map.put("m_no", request.getSession().getAttribute(YogiConstants.M_NO));
		Map<String, Object> result = groupService.selectGroupDetail(map.getMap());
		mv.addObject("gModel",result.get("detail"));
		mv.addObject("cmtList", result.get("cmtList"));
		mv.addObject("geList", result.get("geList"));
		mv.addObject("currentPageNo", map.getCurrentPageNo());
		return mv;
	}
	
	@RequestMapping(value="/likeit", method=RequestMethod.POST)
    public ModelAndView likeit(CommandMap map, HttpServletRequest request) throws Exception{
    	groupService.insertLikeit(map.getMap(),request);
    	return new ModelAndView("redirect:/groupDetail?gg_no="+map.get("gg_no"));
    }
	
	@RequestMapping(value="/enroll", method=RequestMethod.POST)
    public ModelAndView enroll(CommandMap map, HttpServletRequest request) throws Exception{
    	groupService.insertGroupEnroll(map.getMap(),request);
    	return new ModelAndView("redirect:/groupDetail?gg_no="+map.get("gg_no"));
    }
	
	@RequestMapping(value="/comments", method=RequestMethod.POST)
	public ModelAndView insertCmt(CommandMap map, HttpServletRequest request) throws Exception{
		map.put("m_no", request.getSession().getAttribute(YogiConstants.M_NO));
		groupService.insertComments(map.getMap(),request);
		return new ModelAndView("redirect:/groupDetail?gg_no="+map.get("gg_no"));
	}
	
	@RequestMapping(value="/commentsDelete", method=RequestMethod.POST)
	public ModelAndView deleteCmt(CommandMap map, HttpServletRequest request) throws Exception{
		map.put("m_no", request.getSession().getAttribute(YogiConstants.M_NO));
		groupService.deleteComments(map.getMap(), request);
		return new ModelAndView("redirect:/groupDetail?gg_no="+map.get("gg_no"));
	}
	
	@RequestMapping(value="/commentsRep", method=RequestMethod.POST)
	public ModelAndView insertCmtRep(CommandMap map, HttpServletRequest request) throws Exception{
		map.put("m_no", request.getSession().getAttribute(YogiConstants.M_NO));
		groupService.insertComments(map.getMap(),request);

		return new ModelAndView("redirect:/groupDetail?gg_no="+map.get("gg_no"));
	}
	
	//모임 폼 열기
	@RequestMapping(value="/groupForm", method=RequestMethod.GET)
	public String groupForm(Model model) {
		return "groupForm";
	}
		
	//모임 폼 넣기
	@RequestMapping(value="/groupForm", method=RequestMethod.POST)
	public String insert(@ModelAttribute("group") GroupModel group, HttpServletRequest request, BindingResult result) throws Exception{
		System.out.println("GroupController : insertGroup 실행");
		group.setM_no((Integer) request.getSession().getAttribute("session_m_no"));
		groupService.insertGroup(group, request);
		return "redirect:/groupList";
	}
		
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
	
	//모임 수정 폼 열기
	@RequestMapping(value="/modifyForm")
	public ModelAndView groupModifyForm(CommandMap map, HttpServletRequest request) throws Exception{
		
		ModelAndView mv = new ModelAndView("group/groupModifyForm");
		
		Map<String, Object> group = groupService.selectGroupDetail(map.getMap());
		mv.addObject("gModel", group.get("detail"));

		System.out.println(mv.getModel());
		return mv;
	}
	
	@RequestMapping(value="/groupModify")
	public ModelAndView modify(CommandMap map, HttpServletRequest request) throws Exception{
		System.out.println("groupModify:controller 실행");
		ModelAndView mv = new ModelAndView("redirect:/groupDetail?gg_no="+map.get("gg_no"));
		
		Map<String, Object> group = groupService.modifyGroup(map.getMap(), request);

		List<Map<String,Object>> geList = groupDAO.groupEnrollList(map.getMap());
		
		if(geList.size()!=0) { 
			for(int i=0; i<geList.size(); i++) {
				alramService.regAlram(Integer.parseInt(geList.get(i).get("m_no1").toString()),null, 2, Integer.parseInt(map.get("gg_no").toString()));
			}
		}
		mv.addObject("group", group);
		mv.addObject("geList", geList);
		
		return mv;
	}
	
	@RequestMapping(value="/inactivateGroup")
	public ModelAndView inactivateGroup(CommandMap commandMap, HttpServletRequest request) throws Exception{
		ModelAndView mv = new ModelAndView("redirect:/groupList");
		groupService.inactivateGroup(commandMap.getMap());
		
		return mv;
	}
}
