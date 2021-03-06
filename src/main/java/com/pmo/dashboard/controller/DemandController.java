package com.pmo.dashboard.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.xalan.transformer.TransformState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pmo.dashboard.entity.CSDept;
import com.pmo.dashboard.entity.Demand;
import com.pmo.dashboard.entity.HSBCDept;
import com.pmo.dashboard.entity.PageCondition;
import com.pmo.dashboard.entity.User;
import com.pmo.dashboard.util.Constants;
import com.pmo.dashboard.util.Utils;
import com.pom.dashboard.service.CSDeptService;
import com.pom.dashboard.service.DemandService;
import com.pom.dashboard.service.HSBCDeptService;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * 招聘需求的controller
 * 
 * @author tianzhao
 * @version1.0 2017-8-25 14:54:57
 */
@Controller
@RequestMapping(value="/demand")
public class DemandController {
	
	@Resource
	DemandService demandService;
	
	@Resource
	HSBCDeptService hsbcDeptService;
	
	@Resource
	CSDeptService csDeptService;

	private static Logger logger = LoggerFactory.getLogger(DemandController.class);
	
	@RequestMapping("/demandInfo")
	public String demandInfo(){
		return "/demand/demandQuery";
	}
	
	/**
	 * 加载Department信息
	 * @return
	 */
	@RequestMapping("/loadDepartment")
	@ResponseBody
	public List<HSBCDept> loadDepartment(){
		List<HSBCDept> list = hsbcDeptService.queryHSBCDeptName();
		return list;
	}
	
	/**
	 * 加载SubDepartment信息
	 * @param hsbcDeptName
	 * @return
	 */
	@RequestMapping("/loadSubDepartment")
	@ResponseBody
	public List<HSBCDept> loadSubDepartment(String hsbcDeptName){
		List<HSBCDept> list = hsbcDeptService.queryHSBCSubDeptNameByDeptName(hsbcDeptName);
		return list;
	}
	
	/**
	 * 加载交付部信息
	 * @param csBuName
	 * @return
	 */
	@RequestMapping("/loadScSubDeptName")
	@ResponseBody
	public List<CSDept> loadScSubDeptName(String csBuName){
		List<CSDept> list = csDeptService.queryCSSubDeptNameByCsBuName(csBuName);
		return list;
	}
	
	/**
	 * 按条件查询招聘需求和分页功能
	 * @param demand
	 * @param pageCondition
	 * @return
	 */
	@RequestMapping("/queryDemandList")
	@ResponseBody
	public Object demandQuery(String csBuName,Demand demand,PageCondition pageCondition,HttpServletRequest request){
		if("".equals(pageCondition.getCurrPage()) || pageCondition.getCurrPage() == null){
			pageCondition.setCurrPage(1);
			request.getSession().setAttribute("demandCondition", demand);
			request.getSession().setAttribute("demandBUCondition", csBuName);
		}
		
		
		if(!("".equals(pageCondition.getCurrPage()) || pageCondition.getCurrPage() == null)){
			csBuName = (String) request.getSession().getAttribute("demandBUCondition");
			demand = (Demand) request.getSession().getAttribute("demandCondition");
		}
		//String csBuName = request.getParameter("csBuName");
		  User user = (User) request.getSession().getAttribute("loginUser");
	      String userType = user.getUser_type();
		 if(("".equals(demand.getCsSubDept()) || demand.getCsSubDept() == null) &&
	                ("".equals(csBuName) || csBuName == null)){
	            
	            if("1".equals(userType)|| "2".equals(userType)|| "3".equals(userType)|| "4".equals(userType)){
	                csBuName = user.getBu();
	            }
	            
	            if("2".equals(userType)|| "3".equals(userType)|| "4".equals(userType)){
	            	demand.setCsSubDept(csDeptService.queryCSDeptById(user.getCsDeptId()).getCsSubDeptId());
	            }
	            
	        }
		List<Demand> list = demandService.queryDemandList(demand,pageCondition,csBuName,request);
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("list", list);
		result.put("csSubDept", demand.getCsSubDept());
	    result.put("user", user);
		result.put("pageCondition", pageCondition);
		return result;
	}
	
	/**
	 * 导出查询到的需求信息
	 * @param condition
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/exportExcel")
	public HttpServletResponse exportExcel(String condition,HttpServletRequest request,HttpServletResponse response){
		Map<String, Object> params = (Map<String, Object>)request.getSession().getAttribute("demandParams");
		List<Demand> demandList = demandService.queryAllDemand(params);
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String date = sdf.format(new Date());
			
			String fileName =  Constants.PATH+""+Utils.getUUID()+".xls";
			// 创建可写入的Excel工作簿
			
			File file = new File(fileName);
		    File fileDir =new File(Constants.PATH);
		    if(!fileDir.exists()||!fileDir.isDirectory())
			    {
		    	fileDir.mkdir();
			    }
			if(!file.exists()){
			
				file.createNewFile();
			}
			//以fileName为文件名来创建一个Workbook
			WritableWorkbook wwb = Workbook.createWorkbook(file);
			// 创建工作表
			WritableSheet ws = wwb.createSheet("Demand Tracker", 0);
			//List<Demand> list = demandService.queryAllDemand(demand,csBuName);
			//List<Demand> demandList = (List<Demand>)request.getSession().getAttribute("demandList");
			String[] array = condition.split(",");
			for (int i = 0;i < array.length;i++) {
				Label label = new Label(i, 0, array[i]);
				ws.addCell(label);
			}
			for (int i =0 ; i < demandList.size(); i++) {
				int j = 0;
				if(condition.indexOf("RR #")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getRr());
					ws.addCell(lab);
				}
				System.out.println("=="+demandList.get(i).getRr());
				if(condition.indexOf("Job Code")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getJobCode());
					ws.addCell(lab);
				}
				if(condition.indexOf("Tech/Skill")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getSkill());
					ws.addCell(lab);
				}
				if(condition.indexOf("Requestor")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getRequestor());
					ws.addCell(lab);
				}
				if(condition.indexOf("Position")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getPosition());
					ws.addCell(lab);
				}
				if(condition.indexOf("Department")!= -1){
				    Label lab = null;
				    if(demandList.get(i).getHsbcDept() == null){
				        lab = new Label(j++, i+1, "");
				    }else{
				        lab = new Label(j++, i+1, demandList.get(i).getHsbcDept().getHsbcDeptName());
				    }
					ws.addCell(lab);
				}
				if(condition.indexOf("Sub - Department")!= -1){
				    Label lab = null;
                    if(demandList.get(i).getHsbcDept() == null){
                        lab = new Label(j++, i+1, "");
                    }else{
                        lab = new Label(j++, i+1, demandList.get(i).getHsbcDept().getHsbcSubDeptName());
                    }
					ws.addCell(lab);
				}
				if(condition.indexOf("Location")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getLocation());
					ws.addCell(lab);
				}
				if(condition.indexOf("Req published Date")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getReqPublishedDate());
					ws.addCell(lab);
				}
				if(condition.indexOf("Ageing")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getAgeing());
					ws.addCell(lab);
				}
				if(condition.indexOf("No. of Profiles Sent to HSBC")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getProfilesNo());
					ws.addCell(lab);
				}
				if(condition.indexOf("No of Profiles Interviewed")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getInterviewedNo());
					ws.addCell(lab);
				}
				if(condition.indexOf("Status")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getStatus());
					ws.addCell(lab);
				}
				if(condition.indexOf("Candidate Name")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getCandidateName());
					ws.addCell(lab);
				}
				if(condition.indexOf("Proposed Date of Joining")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getProposedJoiningDate());
					ws.addCell(lab);
				}
				if(condition.indexOf("SOW signed")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getSowSigned());
					ws.addCell(lab);
				}
				if(condition.indexOf("BGV Cleared")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getBgvCleared());
					ws.addCell(lab);
				}
				
				if(condition.indexOf("Reason for Abort / Delay")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getReason());
					ws.addCell(lab);
				}
				if(condition.indexOf("Remark")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getRemark());
					ws.addCell(lab);
				}
				if(condition.indexOf("交付部")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getCsSubDept());
					ws.addCell(lab);
				}
				if(condition.indexOf("Planned Onboard date")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getPlannedOnboardDate());
					ws.addCell(lab);
				}
				if(condition.indexOf("DO number")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getDoNumber());
					ws.addCell(lab);
				}
				if(condition.indexOf("HR Priority")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getHrPriority());
					ws.addCell(lab);
				}
				if(condition.indexOf("Completion Day")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getCompletionDay());
					ws.addCell(lab);
				}
				if(condition.indexOf("Onboard Date")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getOnboardDate());
					ws.addCell(lab);
				}
			}
			//写入数据
			wwb.write();
			//关闭文件
			wwb.close();
			
			String filename = "GSV Engagement Dashboard_"+date+".xls";

            // 以流的形式下载文件。
            InputStream fis = new BufferedInputStream(new FileInputStream(fileName));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            // 设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename=" +filename);
            //response.setContentType("application/octet-stream");
            response.setContentType("application/vnd.ms-excel");
            response.addHeader("Content-Length", "" + file.length());
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
            
           file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 需求详情页面信息
	 * @return
	 */
	@RequestMapping("/demandDetail")
	public String demandDetail(String demandId,Model model,HttpServletRequest request){
		
	    Demand demand = demandService.queryDemandById(demandId);
	    
	    
	    
	    model.addAttribute("demand", demand);
	    
		return "/demand/demandDetail";
	}
	
	/**
	 * 员工入场需求页面信息展示
	 * gkf
	 * @return
	 */
	@RequestMapping("/demandOnboard")
	public String demandOnboar(String candidateId,Model model,HttpServletRequest request){
		List<Demand> list = (List<Demand>) request.getSession().getAttribute("demandList");
		for (Demand demand : list) {
			if(candidateId.equals(demand.getCandidateId())){
				model.addAttribute("demand", demand);
				return "/demand/demandOnboard";
			}
		}
		Demand demand = demandService.queryDemandByCandidateId(candidateId);
		
	    model.addAttribute("demand", demand);
	    request.setAttribute("onboardInfo", demand);
		return "/demand/demandOnboard";
	}
	
	/**
	 * gkf 
	 * 员工入场后相应需求信息修改
	 * @param requset
	 * @param demand
	 * @return
	 */
	@RequestMapping("/updateDemandOnBoardById")
	@ResponseBody
	public boolean updateDemandOnBoardById(Demand demand ,HttpServletRequest request) {
		request.getSession().setAttribute("onboardCandidateId", demand.getCandidateId());
		return demandService.updateDemandOnBoardById(demand);
	}
	
	/**
	 * add by jama 
	 * 查询全部交付部
	 * @param csBuName
	 * @return
	 */
	@RequestMapping("/loadAllScSubDeptName")
	@ResponseBody
	public List<CSDept> loadAllScSubDeptName(){
		List<CSDept> list = csDeptService.queryAllCSSubDeptName();
		return list;
	}
	
	/**
	 * 编辑不全需求信息
	 * add by jama
	 * @param demandId
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/demandDetailUpdate")
	public String demandDetailUpdate(String demandId,Model model,HttpServletRequest request){
		List<Demand> list = (List<Demand>) request.getSession().getAttribute("demandList");
		for (Demand demand : list) {
			if(demand.getDemandId().equals(demandId)){
				HSBCDept hSBCDept = hsbcDeptService.queryDemandHSBCSubDeptById(demand.getHsbcSubDeptId());
				if(hSBCDept!=null) {
					if(hSBCDept.getHsbcSubDeptName()==null||"".equals(hSBCDept.getHsbcSubDeptName())) {
						hSBCDept.setHsbcSubDeptName(hSBCDept.getHsbcDeptName());
					}
				}
				demand.setHsbcDept(hSBCDept);
				model.addAttribute("demand", demand);
				return "/demand/demandDetailEdit";
			}
		}
		return "/demand/demandDetailEdit";
	}
}
