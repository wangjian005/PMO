$(function(){
	loadDept();
	loadSkill();
	//loadPosition();
	loadLocation();
	loadStatus();
	//loadHrPriority();
	dateType();
	dateType1();
	loadCsSubDept();
})

/*function addDemand(){
	var url = path+'/service/demand/addDemand';
	alert(url);
	$("#recruitdemandForm").attr("action",url);
	$("#recruitdemandForm").submit();
}*/

$('#rr').blur(function(){
	hrPriority();
});

$('#jobCode').blur(function(){
	hrPriority();
});

function hrPriority(){
	$('#hrPriority').empty();
	if($('#rr').val() == '' || $('#rr').val() == null){
		$('#hrPriority').append("<option>4</option>");
		return; 
	}
	
	if($('#jobCode').val() == '' || $('#jobCode').val() == null){
		$('#hrPriority').append("<option>4</option>");
		return; 
	}
	
	$('#hrPriority').append("<option>3</option>");
}



function loadDept(){
	$.ajax({
		url:path+'/service/hsbcDept/queryDeptName',
		dataType:"json",
		async:true,
		cache:false,
		type:"post",
		success:function(list){
			for(var i = 0;i<list.length;i++){
				$("#hsbcDept").append("<option value='"+list[i].hsbcDeptName+"'>"+list[i].hsbcDeptName+"</option>");
			}
		}
	})
}


$("#hsbcDept").change(function(){
	$('#recruitdemandForm').data("bootstrapValidator").updateStatus("hsbcSubDept",  "NOT_VALIDATED",  null ); 
	 
	var hsbcDeptName = $('#hsbcDept').val();
	$.ajax({
		url:path+'/service/demand/loadSubDepartment',
		dataType:"json",
		async:true,
		data:{"hsbcDeptName":hsbcDeptName},
		cache:false,
		type:"post",
		success:function(list){
			$("#hsbcSubDept").find("option").remove(); 
			$("#hsbcSubDept").append("<option value=''>-- Option --</option>");
			for(var i = 0;i<list.length;i++){
				if(list.length == 1 && list[0].hsbcSubDeptName == null){
					$("#hsbcSubDept").append("<option value='"+$('#hsbcDept').find("option:selected").val()+"'>"+$('#hsbcDept').find("option:selected").text()+"</option>");
				}else{
					for(var i = 0;i<list.length;i++){
						$("#hsbcSubDept").append("<option value='"+list[i].hsbcSubDeptId+"'>"+list[i].hsbcSubDeptName+"</option>");
					}
				}
			}
		}
	})
})

function loadCsSubDept(){
	$.ajax({
		url:path+'/service/csDept/queryAllCSSubDept',
		dataType:"json",
		async:true,
		cache:false,
		type:"post",
		success:function(list){
			for(var i = 0;i<list.length;i++){
				$("#csSubDept").append("<option value='"+list[i].csSubDeptId+"'>"+list[i].csSubDeptName+"</option>");
			}
		}
	})
}

function addDemand(){
	//var isvalid = false;
	//isvalid = $('#recruitdemandForm').data('bootstrapValidator').isValid();
	 var bootstrapValidator = $("#recruitdemandForm").data('bootstrapValidator');
	   bootstrapValidator.validate();
	if(bootstrapValidator.isValid()){
		var demandId=$('#demandId').val();
		var rr=$('#rr').val();
		var jobCode=$('#jobCode').val();
		var skill=$('#skill').val();
		var requestor=$('#requestor').val();
		var position=$('#position').val();
		//部门信息
		var hsbcDept=$('#hsbcDept').val();
		var hsbcSubDept=$('#hsbcSubDept').val();
		var location=$('#location').val();
		var reqPublishedDate=$('#reqPublishedDate2').val();
		var ageing=$('#ageing').val();
		var profilesNo=$('#profilesNo').val();
		var interviewedNo=$('#interviewedNo').val();
		var status=$('#status').val();
		var proposedJoiningDate=$('#proposedJoiningDate').val();
		var dcCleared=$('#dcCleared').val();
		var sowSigned=$('#sowSigned').val();
		var onboarded=$('#onboarded').val();
		var abort=$('#abort').val();
		var delayed=$('#delayed').val();
		var reason=$('#reason').val();
		var nextAction=$('#nextAction').val();
		var status2=$('#status2').val();
		var remark=$('#remark').val();
		var csSubDept=$('#csSubDept').val();
		var plannedOnboardDate=$('#plannedOnboardDate').val();
		var doNumber=$('#doNumber').val();
		var hrPriority=$('#hrPriority').val();
		var reqReceivedDate=$('#reqReceivedDate').val();
		var ageingReceived=$('#ageingReceived').val();
		var demandPriority=$('#demandPriority').val();
		var creatDate=$('#creatDate').val();
		var updateDate=$('#updateDate').val();
		var recruitmentCycle=$('#recruitmentCycle').val();
		var completionDay=$('#completionDay').val();
		var completionDate=$('#completionDate').val();
		var onboardDate=$('#onboardDate').val();
		var staffName=$('#staffName').val();
		
		$.ajax({
			url:path+'/service/demand/addDemand',
			dataType:"json",
	//		data:{"rr":rr,"jobCode":jobCode,"skill":skill,"requestor":requestor},
			data:{"rr":rr,"jobCode":jobCode,"skill":skill,"requestor":requestor,
				"position":position,"location":location,
				"reqPublishedDate":reqPublishedDate,"ageing":ageing,"profilesNo":profilesNo,
				"interviewedNo":interviewedNo,"status":status,"staffName":staffName,
				"proposedJoiningDate":proposedJoiningDate,"dcCleared":dcCleared,"sowSigned":sowSigned,
				"onboarded":onboarded,"abort":abort,"delayed":delayed,"reason":reason,"nextAction":nextAction,
				"status2":status2,"remark":remark,"csSubDept":csSubDept,"plannedOnboardDate":plannedOnboardDate,
				"doNumber":doNumber,"hrPriority":hrPriority,"reqReceivedDate":reqReceivedDate,"ageingReceived":ageingReceived,
				"demandPriority":demandPriority,"creatDate":creatDate,"updateDate":updateDate,"recruitmentCycle":recruitmentCycle,
				"completionDay":completionDay,"completionDate":completionDate,"onboardDate":onboardDate,"hsbcDept":hsbcDept,"hsbcSubDept":hsbcSubDept},
			async:true,
			cache:false,
			type:"post",
			success:function(resultFlag){
				if(resultFlag){
					$("html,body").animate({scrollTop:0}, 500);
					$('#successAlert').html('The information was added successfully').show();
					setTimeout(function () {
						$('#successAlert').hide();
					}, 2000);
				}
			}
		})
	}else{
		//Do something for invalid
		//alert('请填写正确的信息');
		return ;
	}
}

$("#skill").change(function(){
	var skill = $('#skill').val();
	$.ajax({
		url:path+'/service/rate/queryRateBySkill',
		dataType:"json",
		data:{"skill":skill},
		async:true,
		cache:false,
		type:"post",
		success:function(list){
			$("#position").empty();
			$("#position").append("<option value=''>--Option--</option>");
			for(var i = 0;i<list.length;i++){
				$("#position").append("<option>"+list[i].position+"</option>");
			}
		}
	})
	
})


function loadSkill(){
	var url = path+'/json/skill.json'
	$.getJSON(url,  function(data) {
	       $.each(data, function(i, item) {
	    	   $("#skill").append("<option>"+item.name+"</option>");
	       })
	});
}
function loadPosition(){
	var url = path+'/json/role.json'
	$.getJSON(url,  function(data) {
	       $.each(data, function(i, item) {
	    	   $("#position").append("<option>"+item.name+"</option>");
	       })
	});
}
function loadLocation(){
	var url = path+'/json/staffRegion.json'
	$.getJSON(url,  function(data) {
	       $.each(data, function(i, item) {
	    	   $("#location").append("<option>"+item.name+"</option>");
	       })
	});
}

function loadStatus(){
	var url = path+'/json/status.json'
	$.getJSON(url,  function(data) {
	       $.each(data, function(i, item) {
	    	   $("#status").append("<option>"+item.name+"</option>");
	       })
	});
}

function loadHrPriority(){
	var url = path+'/json/hrPriority.json'
	$.getJSON(url,  function(data) {
	       $.each(data, function(i, item) {
	    	   $("#hrPriority").append("<option>"+item.name+"</option>");
	       })
	});
}


function dateType(){
	$('.form_datetime').datetimepicker({
		weekStart: 1,
		minView:'month',
		todayBtn:  1,
		autoclose: 1,
		todayHighlight: 1,
		startView: 2,
		forceParse: 0,
		language:'zh-CN',
		format: 'yyyy-mm-dd',
		pickerPosition: 'bottom-left',
		showMeridian: 1
	}).on('changeDate', function(ev){
		 $('#recruitdemandFormEdit').bootstrapValidator('revalidateField', 'reqPublishedDate1Edit'); 
	});
}

function dateType1(){
	$('.form_datetime1').datetimepicker({
		weekStart: 1,
		minView:'month',
		todayBtn:  1,
		autoclose: 1,
		todayHighlight: 1,
		startView: 2,
		forceParse: 0,
		language:'zh-CN',
		format: 'yyyy-mm-dd',
		pickerPosition: 'bottom-left',
		showMeridian: 1
	}).on('changeDate', function(ev){
		 $('#recruitdemandFormEdit').bootstrapValidator('revalidateField', 'plannedOnboardDate1'); 
	});
}

$("#sub_add").on("click",function(e){
	$(".has-feedback").removeClass("has-feedback");
	$(".has-success").removeClass("has-success");
	$(".has-error").removeClass("has-error");
	$(".form-control-feedback").css("display","none");
	$("small.help-block").css("display","none");
});

