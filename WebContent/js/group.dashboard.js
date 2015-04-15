/**
 * Global RegEx Validators
 */
var reNotLogPass = new RegExp(/[a-zA-Z0-9_]+[a-zA-Z0-9_;:,-\.]*/);
var reEmail = new RegExp(/^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/);
var reYear = new RegExp(/^\d{4}$/);
/* END -- Global RegEx Validators */


/**
 * Load All Groups List functions
 */
function updateGroupsSubFunction(jsonData, callback) {
	$('#groupSelector').html('');
	$('#groupSelector').append('<option value="-1">select group</option>');
	callback(jsonData);
}
function updateGroups () {
	$.post("getgroups", {}, function() {}).done(function(data) {
		var dataTemp = $.parseJSON(data);
    	
		updateGroupsSubFunction(dataTemp, function() {
			for (var i = 0; i < dataTemp.length; i++) {
				var obj = dataTemp[i];
				$('#groupSelector').append('<option value="'+obj['id']+'">'+obj['name']+'</option>');
			}
			$('#groupSelector').selectpicker('refresh');
			$('div.main').html('<h1 class="no-group">No Selected Group</h1>');
		});
	}).fail(function() {
		location.href = "LogoutService?timeout=true";
    });
}
/* END -- Load All Groups List functions */


/**
 * Load Selected Group functions
 */
function updateGroupSubFunction(jsonData, callback) {
	$('div.main').html('<h2 class="sub-header" id="groupTitle" mnemonic="'+jsonData['login']+'">'+jsonData['name']+'</h2>'+
				'<div class="btn-group btn-group-lg toolbox">'+
				'<button type="button" class="btn btn-default" onclick="openAddExistingUsersModal();"><span class="glyphicon glyphicon-download"></span><br/><span class="toolbar-text">Add existing users</span></button>'+
				'<button type="button" class="btn btn-default" onclick="showAddNewSingleUserModal();"><span class="glyphicon glyphicon-plus-sign"></span><br/><span class="toolbar-text">Add new user</span></button>'+
				'<button type="button" class="btn btn-default" onclick="showAddMultipleNewUsersModal();"><span class="glyphicon glyphicon-th-list"></span><br/><span class="toolbar-text">Add multiple new users</span></button>'+
				'<button type="button" class="btn btn-default" onclick="removeUserToolbarClick();"><span class="glyphicon glyphicon-minus-sign"></span><br/><span class="toolbar-text">Remove selected user</span></button>'+
				'</div>'+
				'<table cellpadding="0" cellspacing="0" border="0" class="display" id="groupTable"></table>');
	$('title').html('Groups Dashboard - '+jsonData['name']);
	callback(jsonData);
}
function updateGroup (groupID) {
	$.post("getgroup", {g_id: groupID}, function() {}).done(function(data) {
		var dataTemp = $.parseJSON(data);
		
		
		
		updateGroupSubFunction(dataTemp, function() {
			var groupId = dataTemp['login'];
			$('#groupIdz').html('Group ID: '+'<b>'+groupId+'</b>');
			var obj = dataTemp['users'];
			var tableHeight = ($('.sidebar').outerHeight() - 315)+'px';
			var table = $('#groupTable').DataTable( {
				"scrollY":        ""+tableHeight+"",
				"scrollCollapse": false,
				"paging":         false,
				"order": [[ 1, "asc" ]],
				"aaData": obj,
				"aoColumns": [
				              { "mDataProp": "id" },
				              { "mDataProp": "name" },
				              { "mDataProp": "login" },
				              { "mDataProp": "email" },
				              { "mDataProp": "role"}
				              ],
				"aoColumnDefs": [
				                 { "sTitle": "ID", "aTargets": [0] },
				                 { "sTitle": "Name", "aTargets": [1] },
				                 { "sTitle": "Login", "aTargets": [2] },
				                 { "sTitle": "Email", "aTargets": [3] },
				                 { "sTitle": "Role", "aTargets": [4] }
				                 ]
			});
			$('#groupTable tbody').on( 'click', 'tr', function () {
				if ( $(this).hasClass('selected') ) {
					$(this).removeClass('selected');
				} else {
				    table.$('tr.selected').removeClass('selected');
				    $(this).addClass('selected');
				}
			});
    
			var tableTools = new $.fn.dataTable.TableTools( table, {
				"sSwfPath": "images/copy_csv_xls_pdf.swf",
				"buttons": [
				            "xls",
				            "pdf",
				            { "type": "print", "buttonText": "Print me!" }
				            ]
			});
			    
			$( tableTools.fnContainer() ).insertAfter('#groupTable_info');
			
			$('#groupTable_filter label').append('<button type="button" class="btn btn-default btn-sm" onclick="clearSearchField(this);" style="padding: 4px 10px 4px 10px; margin-bottom: 2px;">clear</button>');
			
			addContextMenuToGroupTable();
		});
	}).fail(function() {
		location.href = "LogoutService?timeout=true";
    });
}
function clearSearchField(clearButton) {
	$(clearButton).parents('label').eq(0).children('input').eq(0).val('').keyup();
}
/* END -- Load Selected Group functions */


/**
 * Group Apps functions
 */
function updateGroupApps (groupID) {
	$.post("getgroupapps", {g_id: groupID}, function() {}).done(function(data) {
		var dataTemp = $.parseJSON(data);
		
		$('#groupApps').html('');
		$('#groupApps').height('200');
		for (var i = 0; i < dataTemp.length; i++) {
			var obj = dataTemp[i];
			$('#groupApps').append('<li class="list-group-item"><div class="checkbox"><label><input type="checkbox" value="'+obj['id']+'" '+(obj['checked'] == '1' ? 'checked' : '')+'> '+obj['name']+'</label></div></li>');
		}
		$('#groupApps input[type="checkbox"]').change(function() {
			$('#updateGroupAppsOKBtn').removeClass('hidden');
		}); 
	}).fail(function() {
		location.href = "LogoutService?timeout=true";
    });
}
function updateGroupAppsOKBtnClick () {
	updateGroupAppsOKBtnClickSubFunction(updateGroupAppsOKBtnClickSubFunction2);
}
function updateGroupAppsOKBtnClickSubFunction (callback) {
	var appsJSON = new Array();
	$('#groupApps input').each(function() {
		var curID = $(this).val();
		if ($(this).prop("checked")) {
			appsJSON.push({"id" : ""+curID+""});
		}
	});
	updateGroupAppsOKBtnClickSubFunction2(JSON.stringify(appsJSON));
}
function updateGroupAppsOKBtnClickSubFunction2 (appsJSON) {
	var groupMnemonic = $('#groupTitle').attr('mnemonic');
	$.post("updategroupapps", {g_m: groupMnemonic, apps: appsJSON}, function() {}).done(function(data) {
		var dataTemp = $.parseJSON(data);
		if ('error' in dataTemp) {
			notifyError(dataTemp['error']);
		} else if ('success' in dataTemp) {
			notifySuccess(dataTemp['success']);
			$('#updateGroupAppsOKBtn').addClass('hidden');
		}
	}).fail(function() {
		location.href = "LogoutService?timeout=true";
    });
}
/* END -- Group Apps functions */


/**
 * Group Course functions
 */
function updateGroupCourses (groupID) {
	$.post("getgroupcourses", {g_id: groupID}, function() {}).done(function(data) {
		var dataTemp = $.parseJSON(data);
		if ('error' in dataTemp) {
			notifyError(dataTemp['error']);
			$('#groupCourses').html('None');
			$('#groupCourses').height('30');
			$('#addGroupCoursePlusBtn').addClass('hidden');
		} else {
			if ('groupcourses' in dataTemp) {
				$('#groupCourses').html('');
				$('#groupCourses').height('120');
				var groupCourses = dataTemp['groupcourses'];
				for (var i = 0; i < groupCourses.length; i++) {
					var obj = groupCourses[i];
					$('#groupCourses').append('<li class="list-group-item" courseid="'+obj['id']+'">'+obj['title']+'<b>['+obj['id']+']</b>'+'</li>');
				}
				$('#addGroupCoursePlusBtn').removeClass('hidden');
				$('#addGroupCoursePlusBtn span').removeClass('glyphicon glyphicon-plus-sign').addClass('glyphicon glyphicon-pencil');
				$('#addCourseModal .modal-title').html('Edit Course For The Group');
				$('#addCourseModal .modal-footer').children().eq(0).html('Edit');
			} else {
				$('#groupCourses').html('None');
				$('#groupCourses').height('30');
				$('#addGroupCoursePlusBtn span').removeClass('glyphicon glyphicon-pencil').addClass('glyphicon glyphicon-plus-sign');
				$('#addGroupCoursePlusBtn').removeClass('hidden');
				$('#addCourseModal .modal-title').html('Add Course To The Group');
				$('#addCourseModal .modal-footer').children().eq(0).html('Add');
			}
			if ('courses' in dataTemp) {
				$('#addCourseCourseField').html('');
				var courses = dataTemp['courses'];
				for (var i = 0; i < courses.length; i++) {
					var obj = courses[i];
					$('#addCourseCourseField').append('<option value="'+obj['id']+'">'+obj['name']+' {'+obj['id']+'}</option>');
				}			
			}			
		}
	}).fail(function() {
		location.href = "LogoutService?timeout=true";
    });
}
function openAddCourseModal () {
	$('#addCourseModal').modal('show');
}
function addCourseToTheGroup () {
	var groupMnemonic = $('#groupTitle').attr('mnemonic');
	var groupName = $('#groupTitle').html();
	var courseTemp = $('#addCourseCourseField').val();
	var termTemp = $('#addCourseTermField').val();
	var yearTemp = $('#addCourseYearField').val();
	if (!reYear.test(yearTemp)) {
		alertAddCourseError('Year has to be in 4 digit format');
    } else {
    	$.post("addcoursetothegroup", {g_m: groupMnemonic, g_n: groupName, c_id: courseTemp, term: termTemp, year: yearTemp}, function() {}).done(function(data) {
    		var dataTemp = $.parseJSON(data);
    		$('#addCourseModal').modal('hide');
    		if ('error' in dataTemp) {
    			notifyError(dataTemp['error']);
    		} else if ('success' in dataTemp) {
    			notifySuccess(dataTemp['success']);
			}
    		updateGroupCourses($('#groupSelector').val());
    	}).fail(function() {
    		location.href = "LogoutService?timeout=true";
        });
    }
}
function alertAddCourseError (text) {
	$("#alertMessageAddCourse").fadeOut(0).html('<div class="alert alert-danger" role="alert">'+text+'</div>').fadeIn(1000).delay(3000).fadeOut(1000);	
}
/* END -- Group Course functions */


/**
 * Existing Users functions
 */
function updateUsersSubFunction(jsonData, callback) {
	$('#addExistingUsersModalBody').html('<div class="btn-group btn-group-lg toolbox">'+
	'<button type="button" class="btn btn-default" onclick="seeSelectedUsers();"><span class="glyphicon glyphicon-eye-open"></span><br/><span class="toolbar-text">See selected</span></button>'+
	'<button type="button" class="btn btn-default" onclick="addSelectedUsers();"><span class="glyphicon glyphicon-plus-sign"></span><br/><span class="toolbar-text">Add selected</span></button>'+
	'<button type="button" class="btn btn-default" onclick="deselectUsersTable();"><span class="glyphicon glyphicon-unchecked"></span><br/><span class="toolbar-text">Deselect all</span></button>'+
	'</div>'+
	'<table cellpadding="0" cellspacing="0" border="0" class="display" id="usersTable"></table>');
	callback(jsonData);
}
function updateUsers () {
	$.post("getallusers", {}, function() {}).done(function(data) {
		var dataTemp = $.parseJSON(data);
	    	
		updateUsersSubFunction(dataTemp, function() {
			var tableU = $('#usersTable').DataTable( {
				"scrollY":        "250px",
				"scrollCollapse": false,
				"paging":         false,
				"order": [[ 1, "asc" ]],
				"aaData": dataTemp,
				"aoColumns": [
				              { "mDataProp": "id" },
				              { "mDataProp": "name" },
				              { "mDataProp": "login" },
				              { "mDataProp": "email" }
				              ],
				 "aoColumnDefs": [                              
				                  { "sTitle": "ID", "aTargets": [0] },    
				                  { "sTitle": "Name", "aTargets": [1] },
				                  { "sTitle": "Login", "aTargets": [2] },
				                  { "sTitle": "Email", "aTargets": [3] }
				                  ]
			});
    		
			$('#usersTable tbody').on( 'click', 'tr', function () {
				$(this).toggleClass('selected');
			});
			
			$('#usersTable_filter label').append('<button type="button" class="btn btn-default btn-sm" onclick="clearSearchField(this);" style="padding: 4px 10px 4px 10px; margin-bottom: 2px;">clear</button>');
	    		
			$('#usersTable tr[role="row"]').on('contextmenu', function(e) {
				$(".custom-menu-user").finish().toggle(100).css({
					top: e.pageY + "px",
					left: e.pageX + "px"
				});
				var rowChildren = $(this).children('td');
				var id = rowChildren.eq(0).html();
				var name = rowChildren.eq(1).html();
				var login = rowChildren.eq(2).html();
				var email = rowChildren.eq(3).html();
				$('.custom-menu-user li[data-action="add"]').unbind('click').click({param1: id, param2: name, param3: login, param4: email}, function(params){
					$(".custom-menu-user").hide(100);
					addUserToGroup(params.data.param1, params.data.param2, params.data.param3, params.data.param4, "student");
				});
				return false;
			});
		});
	}).fail(function() {
		location.href = "LogoutService?timeout=true";
    });
}
function deselectUsersTable() {
	$('#usersTable tr.selected').removeClass('selected');
}
function seeSelectedUsers() {
	if ($('#usersTable tr.selected').length) {
		var tableString = '<table class="table table-striped"><thead><tr><th>ID</th><th>Name</th><th>Login</th><th>Email</th></tr></thead><tbody>';
		$('#usersTable tr.selected').each(function() {
			var curRow = $(this).children('td');
			tableString += '<tr><td>'+curRow.eq(0).html()+'</td><td>'+curRow.eq(1).html()+'</td><td>'+curRow.eq(2).html()+'</td><td>'+curRow.eq(3).html()+'</td></tr>';
		});
		tableString += '</tbody></table>';
		
		$('#seeSelectedUsersModalTitle').html('Selected Users ('+$('#usersTable tr.selected').length+' total)');
		$('#seeSelectedUsersModalBody').html(tableString);
		$('#seeSelectedUsersModal').modal('show');		
	} else {
		notifyErrorShort("You don't have any selected users");
	}
}
/* END -- Existing Users functions */


/**
 * Document ready
 */
$(document).ready(function() {

	$('#groupSelector').selectpicker();
	$('#groupSelector').on('change', function(){
		if ($('#groupSelector').val() != '-1') {
			updateGroup($('#groupSelector').val());
			
			updateGroupApps($('#groupSelector').val());
			updateGroupCourses($('#groupSelector').val());
		} else {
			$('div.main').html('<h1 class="no-group">No Selected Group</h1>');
			$('#groupApps').html('No Selected Group');
			$('#groupApps').height('30');
			$('#groupCourses').html('No Selected Group');
			$('#groupCourses').height('30');
			$('#addGroupCoursePlusBtn').addClass('hidden');
			$('title').html('Groups Dashboard');
		}
		$('#updateGroupAppsOKBtn').addClass('hidden');
	});
	updateGroups();
	  
	// If the document is clicked somewhere
	$(document).bind("mousedown", function (e) {
		// If the clicked element is not the menu
		if (!$(e.target).parents(".custom-menu").length > 0) {
			$(".custom-menu").hide(100);
		}
		if (!$(e.target).parents(".custom-menu-user").length > 0) {
			$(".custom-menu-user").hide(100);
		}
		$('#suggestions').fadeOut();
	});

	$('#notificationCenterModalBody').height($('.sidebar').outerHeight() - 200);
	
	$('#searchField').on('focus', function(){
		lookup(this.value);
	});
	
	$('#overrideUserPassModal').on('shown.bs.modal', function () {
		$('#updateUserPassInputField').val('');
	    $('#updateUserPassInputField').focus();
	});
	
	$('#addNewSingleUserModal').on('shown.bs.modal', function () {
	    $('#userAddName').focus();
	});
	
	$('#addMultipleNewUsersModal').on('shown.bs.modal', function () {
	    $('#multipleNewUsersTextArea').focus();
	});
	
	$('#addGroupModal').on('shown.bs.modal', function () {
	    $('#addGroupNameField').focus();
	});
});
/* END -- Document ready */


/**
 * Add new Group functions
 */
function openAddGroupModal () {
	$('#addGroupModal').modal('show');
}
function createNewGroup () {
	var gName = $('#addGroupNameField').val();
	var gMnemonic = $('#addGroupMnemonicField').val();
	var mnemonicRegex = /^[a-zA-Z0-9\-]+$/;
	var names = new Array();
	$('#groupSelector option').each(function(){
		names.push($(this).html());
	});

	if (gName.length < 1) {
		alertAddGroupError('Group name can not be empty');
		return;
	} else if (gName.length > 60) {
		alertAddGroupError('Group name can not be longer than 60 characters');
		return;
	} else if ($.inArray(gName, names) > -1) {
		alertAddGroupError('Group with such name already exist');
		return;
	} else if (gMnemonic.length < 1) {
		alertAddGroupError('Group mnemonic can not be empty');
		return;
	} else if (gMnemonic.length > 30) {
		alertAddGroupError('Group mnemonic can not be longer than 30 characters');
		return;
	} else if (!mnemonicRegex.test(gMnemonic)) {
		alertAddGroupError('Only characters a-z and "-" are  acceptable for mnemonic');
		return;
	} else {
		$.post("addgroup", {g_m: gMnemonic, g_n: gName}, function() {}).done(function(data) {
			var dataTemp = $.parseJSON(data);
			if ('error' in dataTemp) {
				notifyError(dataTemp['error']);
				alertAddGroupError(dataTemp['error']);
			} else if ('success' in dataTemp) {
				notifySuccess(dataTemp['success']);
				updateGroups();
				$('#addGroupModal').modal('hide');
				$('#addGroupNameField').val('');
				$('#addGroupMnemonicField').val('');
				$('div.main').html('<h1 class="no-group">No Selected Group</h1>');
				$('#groupApps').html('No Selected Group');
				$('#groupApps').height('30');
				$('#groupCourses').html('No Selected Group');
				$('#groupCourses').height('30');
				$('title').html('Groups Dashboard');
			}
		}).fail(function() {
			location.href = "LogoutService?timeout=true";
	    });
	}
}
function alertAddGroupError (text) {
	$("#alertMessageAddGroup").fadeOut(0).html('<div class="alert alert-danger" role="alert">'+text+'</div>').fadeIn(1000).delay(3000).fadeOut(1000);	
}
/* END -- Add new Group functions */


function openAddExistingUsersModal () {
	$('#addExistingUsersModalBody').html('Loading...');
	$('#addExistingUsersModal').modal('show');
}
$('#addExistingUsersModal').on('shown.bs.modal', function (e) {
	updateUsers();
});


/**
 * Remove user from the Group functions
 */
function removeUserToolbarClick () {
	var rowChildren = $('#groupTable tr.selected').children('td');
	if (rowChildren.length) {
		var id = rowChildren.eq(0).html();
		var name = rowChildren.eq(1).html();
		var login = rowChildren.eq(2).html();
		
		if (showDeleteConfirmationDialogFlag) {
			removeUserConfirmation(id, name, login);
		} else {
			removeUserFromGroup(id, name, login);
		}
	} else {
		notifyErrorShort('No user was selected');
	}
}
function removeUserFromGroup(rID, rName, rLogin) {
	$('#confirmDeleteUserModal').modal('hide');
	var groupMnemonic = $('#groupTitle').attr('mnemonic');
	$.post("removeuserfromgroup", {g_m: groupMnemonic, u_l: rLogin}, function() {}).done(function(data) {
		var dataTemp = $.parseJSON(data);
		if ('error' in dataTemp) {
			notifyError(dataTemp['error']);
		} else if ('success' in dataTemp) {
			notifySuccess(dataTemp['success']);
			$('#groupTable tr td:nth-child(3)').each(function() {
				if ($(this).html() == rLogin) {
					var table = $('#groupTable').dataTable();
					var aPos = table.fnGetPosition( $(this).parents('tr').eq(0)[0] );
					table.fnDeleteRow(aPos);
				 }
			});
		}
	}).fail(function() {
		location.href = "LogoutService?timeout=true";
    });
}
function removeUserConfirmation (rID, rName, rLogin) {
	$('#confirmDeleteUserModalTitle').html('Are you sure you want to delete <span style="color: gray;">'+rName+'</span> from this group?');
	$('#confirmDeleteUserModalYes').attr('onclick', "removeUserFromGroup('"+rID+"', '"+rName+"', '"+rLogin+"');");
	$('#confirmDeleteUserModal').modal('show');
}
function toggleDeleteConfirmation() {
	showDeleteConfirmationDialogFlag = !showDeleteConfirmationDialogFlag;
	var updateConfirmationInUserBean = showDeleteConfirmationDialogFlag.toString();
	$.post("removeuserfromgroup", {w_m: updateConfirmationInUserBean}, function() {});
}
/* END -- Remove user from the Group functions */


/**
 * Add Single User to the Group functions
 */
function addUserToGroup(rID, rName, rLogin, rEmail, rRole) {
	var groupMnemonic = $('#groupTitle').attr('mnemonic');
	$.post("addexistingusertothegroup", {g_m: groupMnemonic, u_l: rLogin}, function() {}).done(function(data) {
		var dataTemp = $.parseJSON(data);
		if ('error' in dataTemp) {
			notifyError(dataTemp['error']);
		} else if ('success' in dataTemp) {
			notifySuccess(dataTemp['success']);
			//alert(rRole);
			$('#groupTable').dataTable().fnAddData( {"id" : ""+rID+"", "name" : ""+rName+"", "login" : ""+rLogin+"", "email" : ""+rEmail+"", "role" : ""+rRole+""});
			addContextMenuToGroupTable();
		} else if ('notice' in dataTemp) {
			notifyDefault(dataTemp['notice']);
		}
	}).fail(function() {
		location.href = "LogoutService?timeout=true";
    });
}
function addUserTopSearch(addButton) {
	var userInfo = $(addButton).parents('a').eq(0);
	var rID = userInfo.attr('uid');
	var rName = userInfo.attr('uname');
	var rLogin = userInfo.attr('ulogin');
	var rEmail = userInfo.attr('uemail');
	addUserToGroup(rID, rName, rLogin, rEmail);
}
/* END -- Add Single User to the Group functions */


/**
 * Add Multiple Existing Users to the Group functions
 */
function addSelectedUsersSubFunction (callback) {
	var usersTemp = new Array();
	$('#usersTable tr.selected').each(function() {
		var curRow = $(this).children('td');
		usersTemp.push({"login" : ""+curRow.eq(2).html()+""});
	});
	callback(JSON.stringify(usersTemp));
}
function addSelectedUsers () {
	if ($('#usersTable tr.selected').length) {
		addSelectedUsersSubFunction(addMultipleUsersToGroup);
	} else {
		notifyErrorShort("You don't have any selected users");
	}
}
function addMultipleUsersToGroup(usersJson) {
	var groupMnemonic = $('#groupTitle').attr('mnemonic');
	$.post("addexistinguserstothegroup", {g_m: groupMnemonic, u_l: usersJson}, function() {}).done(function(data) {
		var dataTemp = $.parseJSON(data);
		if ('error' in dataTemp) {
			notifyError(dataTemp['error']);
		} else {
			if ('success' in dataTemp) {
				var successObj = dataTemp['success'];
				var addCount = 0;
				for(var i in successObj)
				{
				     var text = successObj[i].success;
				     notificationCenterAddRecord(text, 'success');
				     $('#groupTable').dataTable().fnAddData( {"id" : ""+successObj[i].userID+"", "name" : ""+successObj[i].userName+"", "login" : ""+successObj[i].userLogin+"", "email" : ""+successObj[i].userEmail+"", "role": "student"});
				     addCount++;				     
				}
				if (addCount > 0) {
					if (addCount == 1) {
						notifySuccess('1 user was added to the group');
					} else {
						notifySuccess(addCount+' users were added to the group');						
					}
					addContextMenuToGroupTable();
				}
			} 
			if ('exist' in dataTemp) {
				var existObj = dataTemp['exist'];
				var existCount = 0;
				for(var i in existObj)
				{
				     var text = existObj[i].notice;
				     notificationCenterAddRecord(text, 'warning');
				     existCount++;
				}
				if (existCount > 0) {
					if (existCount == 1) {
						notifyDefault('1 user was already in the group');	
					} else {
						notifyDefault(existCount+' users were already in the group');						
					}				
				}
			}
			if ('exception' in dataTemp) {
				var exceptionObj = dataTemp['exception'];
				var notFoundCount = 0;
				var sqlCount = 0;
				for(var i in exceptionObj)
				{
					if (exceptionObj[i].hasOwnProperty('notfound')) {
						var text = exceptionObj[i].notfound;
						notificationCenterAddRecord(text, 'danger');
						notFoundCount++;
						
					} else if (exceptionObj[i].hasOwnProperty('sql')) {
						var text = exceptionObj[i].sql;
						notificationCenterAddRecord(text, 'danger');
						sqlCount++;
					}
				}
				if (notFoundCount > 0) {
					if (notFoundCount == 1) {
						notifyError('1 user was not found in the database');
					} else {
						notifyError(notFoundCount+' users were not found in the database');						
					}
				}
				if (sqlCount > 0) {
					if (sqlCount == 1) {
						notifyError('1 user was not added to the group, because of the sql exeption during update');
					} else {
						notifyError(sqlCount+' users were not added to the group, because of thr sql exeption during update');					
					}				
				}
			}
			deselectUsersTable();
		}
	}).fail(function() {
		location.href = "LogoutService?timeout=true";
    });
}
function addSelectedUsersFromSelectedModal () {
	addSelectedUsers();
	$('#seeSelectedUsersModal').modal('hide');
}
/* END -- Add Multiple Existing Users to the Group functions */


/**
 * Context Menu function
 */
function addContextMenuToGroupTable() {
	$('#groupTable tr[role="row"]').unbind('contextmenu');
	$('#groupTable tr[role="row"]').on('contextmenu', function(e) {
		$(".custom-menu").finish().toggle(100).css({
			top: e.pageY + "px",
			left: e.pageX + "px"
		});
		var rowChildren = $(this).children('td');
		var id = rowChildren.eq(0).html();
		var name = rowChildren.eq(1).html();
		var login = rowChildren.eq(2).html();
		var role = rowChildren.eq(4).html();
		
		$('.custom-menu li[data-action="delete"]').unbind('click').click({param1: id, param2: name, param3: login}, function(params){
			$(".custom-menu").hide();
			if (showDeleteConfirmationDialogFlag) {
				removeUserConfirmation(params.data.param1, params.data.param2, params.data.param3);
			} else {			
				removeUserFromGroup(params.data.param1, params.data.param2, params.data.param3);
			}
		});
		$('.custom-menu li[data-action="info"]').unbind('click').click({param1: login}, function(params){
			$(".custom-menu").hide();
			openUserInfoModal(params.data.param1, userInfoStaticMode);
			$('#userEditRoleStatic').html(role);
			
		});
		if ($('.custom-menu li[data-action="edit"]').length) {
			$('.custom-menu li[data-action="edit"]').unbind('click').click({param1: login}, function(params){
				$(".custom-menu").hide();
				openUserInfoModal(params.data.param1, userInfoEditMode);
				//alert(role);
				$('#userEditRole input:radio[value="'+role+'"]').prop('checked',true);
			});			
		}
		return false;
	});
}
/* END -- Context Menu function */


/**
 * Notification functions
 */
function notifySuccess(text) {
	$.ambiance({message: text, type: "success", timeout: 6});
	notificationCenterAddRecord(text, 'success');
}
function notifyError(text) {
	$.ambiance({message: text, type: "error", timeout: 6});
	notificationCenterAddRecord(text, 'danger');
}
function notifyErrorShort(text) {
	$.ambiance({message: text, type: "error"});
	notificationCenterAddRecord(text, 'danger');
}
function notifyDefault(text) {
	$.ambiance({message: text, timeout: 6});
	notificationCenterAddRecord(text, 'warning');
}
//For todays date;
Date.prototype.today = function () { 
    return ((this.getDate() < 10)?"0":"") + this.getDate() +"/"+(((this.getMonth()+1) < 10)?"0":"") + (this.getMonth()+1) +"/"+ this.getFullYear();
};
// For the time now
Date.prototype.timeNow = function(){
	return ((this.getHours() < 10)?"0":"") + ((this.getHours()>12)?(this.getHours()-12):this.getHours()) +":"+ ((this.getMinutes() < 10)?"0":"") + this.getMinutes() +":"+ ((this.getSeconds() < 10)?"0":"") + this.getSeconds() + ((this.getHours()>12)?('PM'):'AM');
};
//Date and time now
function getDateAndTimeNow() {
	var newDate = new Date();
	return newDate.today() + " " + newDate.timeNow();
}
function openNotificationCenter() {
	$('#notificationCenterModal').modal('show');
}
function notificationCenterAddRecord(text, nType) {
	$('#notificationCenterModalBody').prepend('<div class="alert alert-'+nType+'"><span style="font-size: 0.8em; color: gray;">'+(new Date().timeNow())+'</span> '+text+'</div>');
}
/* END -- Notification functions */


/**
 * User Search functions
 */
function lookup(inputString) {
	inputString = inputString.trim();
	if(inputString.length < 2) {
		$('#suggestions').fadeOut();
		$('#searchclear').css('opacity','0');
	} else {
		var addButtonFlag = false;
		if ($('#groupTitle').length) {
			addButtonFlag = true;
		}
		$.post("searchuser", {queryString: ""+inputString+"", showAdd : addButtonFlag}, function(data) {
			$('#suggestions').fadeIn();
			$('#searchclear').css('opacity','1');
			$('#suggestions').html(data);
		}).fail(function() {
			location.href = "LogoutService?timeout=true";
	    });
	}
}
function clearTopUserSearchField () {
	$('#searchclear').css('opacity','0');
	$('#searchField').val('');
}
/*END -- User Search functions */


/**
 * User Info And Edit functions
 */
function openUserInfoModal(userLogin, callback) {
	$.post("getuserinfo", {u_l: userLogin}, function() {}).done(function(data) {
		var dataTemp = $.parseJSON(data);
		if ('error' in dataTemp) {
			notifyError(dataTemp['error']);
		} else if ('name' in dataTemp) {
			$('#userInfoModalTitle').html(dataTemp['name']);
			$('#userEditNameStatic').html(dataTemp['name']);
			$('#userEditName').val(dataTemp['name']);
			$('#userEditLoginStatic').html(dataTemp['login']);
			$('#userEditEmailStatic').html(dataTemp['email']);
			$('#userEditEmail').val(dataTemp['email']);
			$('#userEditOrganizationStatic').html(dataTemp['organization']);
			$('#userEditOrganization').val(dataTemp['organization']);
			$('#userEditCityStatic').html(dataTemp['city']);
			$('#userEditCity').val(dataTemp['city']);
			$('#userEditCountryStatic').html(dataTemp['country']);
			$('#userEditCountry').val(dataTemp['country']);
			if ($('#userEditTeacher').length && 'teacher' in dataTemp) {
				$('#userEditTeacher').prop('checked',(dataTemp['teacher'] == '1'));
			}
			$('#userInfoModal').modal('show');
			
			callback();
		}
	}).fail(function() {
		location.href = "LogoutService?timeout=true";
    });
}
function userInfoStaticMode() {
	$('#userEditNameStatic').show();
	$('#userEditName').hide();
	$('#userEditEmailStatic').show();
	$('#userEditEmail').hide();
	$('#userEditOrganizationStatic').show();
	$('#userEditOrganization').hide();
	$('#userEditCityStatic').show();
	$('#userEditCity').hide();
	$('#userEditCountryStatic').show();
	$('#userEditCountry').hide();
	$('#userEditRoleStatic').show();
	$('#userEditRole').hide();
	if ($('#userInfoEditButton').length) {
		$('#userInfoEditButton').show();
	}
	if ($('#editUsersSaveBtn').length) {
		$('#editUsersSaveBtn').hide();
	}
}
function userInfoEditMode() {
	$('#userEditNameStatic').slideUp('slow');
	$('#userEditName').slideDown('slow');
	$('#userEditEmailStatic').slideUp('slow');
	$('#userEditEmail').slideDown('slow');
	$('#userEditOrganizationStatic').slideUp('slow');
	$('#userEditOrganization').slideDown('slow');
	$('#userEditCityStatic').slideUp('slow');
	$('#userEditCity').slideDown('slow');
	$('#userEditCountryStatic').slideUp('slow');
	$('#userEditCountry').slideDown('slow');
	$('#userEditRoleStatic').slideUp('slow');
	$('#userEditRole').slideDown('slow');
	
	if ($('#userInfoEditButton').length) {
		$('#userInfoEditButton').hide();
	}
	if ($('#editUsersSaveBtn').length) {
		$('#editUsersSaveBtn').show();
	}
	$("#userEditName").keyup();
}
function editUserSaveBtnUpdate () {
	if ($('#userEditName').val().length == 0 || $('#userEditName').val().length > 60) {
		if(!$('#userEditName').hasClass('error')) {
			$('#userEditName').addClass('error');
		}
	} else {
		if($('#userEditName').hasClass('error')) {
			$('#userEditName').removeClass('error');
		}
	}
	if (!reEmail.test($('#userEditEmail').val())) {
		if(!$('#userEditEmail').hasClass('error')) {
			$('#userEditEmail').addClass('error');
		}
	} else {
		if($('#userEditEmail').hasClass('error')) {
			$('#userEditEmail').removeClass('error');
		}
	}
	if ($('#userEditName.error').length || $('#userEditEmail.error').length) {
		$('#editUsersSaveBtn').attr('disabled', 'disabled');
	} else {
		$('#editUsersSaveBtn').removeAttr('disabled');
	}
}
function saveUserEditUpdates () {
	var name = $('#userEditName').val();
	var login = $('#userEditLoginStatic').html();
	var email = $('#userEditEmail').val();
	var organization = $('#userEditOrganization').val();
	var city = $('#userEditCity').val();
	var country = $('#userEditCountry').val();	
	var role = $("input[name='radioGroup']:checked", "#userInfoModalModalBody").val();
	var groupid = $('#groupIdz b').html();
	
	var teacher = '0';
	if ($('#userEditTeacher').length) {
		if ($('#userEditTeacher').prop("checked")) {
			teacher = '1';					
		}
	}
	$.post("updateuserinfo", {u_n: name, u_e: email, u_l: login, u_o: organization, u_c: city, u_country: country, u_r: role, u_gi: groupid}, function() {}).done(function(data) {
		var dataTemp = $.parseJSON(data);
		if ('error' in dataTemp) {
			notifyError(dataTemp['error']);
		} else if ('success' in dataTemp) {
			notifySuccess(dataTemp['success']);
			$('#userInfoModal').modal('hide');
			if ($('#groupTable').length && 'user' in dataTemp) {
				var id = dataTemp['user'].id;
				name = dataTemp['user'].name;
				login = dataTemp['user'].login;
				email = dataTemp['user'].email;
				role = dataTemp['role'];
				$('#groupTable tbody tr').each(function() {
					var rowChildrenTds = $(this).children('td');
					if (rowChildrenTds.eq(2).html() == login) {				
						rowChildrenTds.eq(0).html(id); 
						rowChildrenTds.eq(1).html(name); 
						rowChildrenTds.eq(3).html(email); 
						rowChildrenTds.eq(4).html(role);
						
						var table = $('#groupTable').DataTable();
						table.draw();
					}
				});
			}
		}
	}).fail(function() {
		location.href = "LogoutService?timeout=true";
    });
}
/* END -- User Info And Edit functions */


/**
 * Password Override functions
 */
function showUpdateUserPassModal () {
	$('#overrideUserPassModal').modal('show');
}
function updateUserPassSaveButton () {
	var userPass = $('#updateUserPassInputField').val().trim();
	if (userPass != null && userPass.length > 4 && userPass.length < 60) {
		$('#saveUserPassButton').removeAttr('disabled');
	} else {
		$('#saveUserPassButton').attr('disabled', 'disabled');
	}
}
function updateUserPassword () {
	var userPass = $('#updateUserPassInputField').val().trim();
	var userLogin = $('#userEditLoginStatic').html();
	$.post("overridepassword", {u_l: userLogin, u_p: userPass}, function() {}).done(function(data) {
		var dataTemp = $.parseJSON(data);
		if ('error' in dataTemp) {
			notifyError(dataTemp['error']);
		} else if ('success' in dataTemp) {
			notifySuccess(dataTemp['success']);
		}
		$('#overrideUserPassModal').modal('hide');
	}).fail(function() {
		location.href = "LogoutService?timeout=true";
    });
}
/* END -- Password Override functions */


/**
 * Add New Single User functions
 */
function showAddNewSingleUserModal() {
	$('#addNewSingleUserModal').modal('show');
}
function addNewUser () {
	var errorMessage = "";
	var name = $('#userAddName').val().trim();
	var email = $('#userAddEmail').val().trim();
	var login = $('#userAddLogin').val().trim();
	var password = $('#userAddPass').val().trim();
	var organization = $('#userAddOrganization').val().trim();
	organization = organization == null ? '' : organization;
	var city = $('#userAddCity').val().trim();
	city = city == null ? '' : city;
	var country = $('#userAddCountry').val().trim();
	country = country == null ? '' : country;
	var role = $("input[name='radioGroup']:checked", "#addNewSingleUserModal").val();
	
	
	if (name.length == 0 || name.length > 60) {
		errorMessage = "Name has to be 1 to 60 characters long";
	}
	if (!reEmail.test(email)) {
		errorMessage = errorMessage.length > 0 ? errorMessage+"<br/>" : errorMessage;
		errorMessage += "Invalid email format";
    }
	if (!reNotLogPass.test(login)) {
		errorMessage = errorMessage.length > 0 ? errorMessage+"<br/>" : errorMessage;
		errorMessage += "Login can have only alphanumerical symbols and '_' , '-' and '.'";
		
    }
	if (password.length == 0 || password.length > 60) {
		errorMessage = errorMessage.length > 0 ? errorMessage+"<br/>" : errorMessage;
		errorMessage += "Password has to be 1 to 60 characters long";
	}
//	if ($('#userAddTeacher').length) {
//		if ($('#userAddTeacher').prop("checked")) {
//			teacher = '1';					
//		}
//	}
	if (errorMessage.length > 0) {
		$("#addNewSingleUserErrorBox").hide().html('<div class="alert alert-danger alert-dismissible" role="alert">'+
				'<button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>'+
				errorMessage+'</div>').fadeIn('slow');
	} else {
		var groupMnemonic = $('#groupTitle').attr('mnemonic');
		$('#addNewSingleUserModal').modal('hide');
		$.post("addnewusertothegroup", {g_m: groupMnemonic, u_n: name, u_e: email, u_l: login, u_p: password, u_o: organization, u_c: city, u_country: country, u_r: role}, function() {}).done(function(data) {
			var dataTemp = $.parseJSON(data);
			if ('error' in dataTemp) {
				notifyError(dataTemp['error']);
			} else {
				if ('success' in dataTemp) {
					notifySuccess(dataTemp['success']);					
				} else if ('warning' in dataTemp) {
					notifyDefault(dataTemp['warning']);					
				}
				var userObj = dataTemp['user'];
				addUserToGroup(userObj['id'], userObj['name'], userObj['login'], userObj['email'], userObj['role']);//changed
				$('#userAddName').val('');
				$('#userAddEmail').val('');
				$('#userAddLogin').val('');
				$('#userAddPass').val('');
				$('#userAddOrganization').val('');
				$('#userAddCity').val('');
				$('#userAddCountry').val('');
				$('#userAddTeacher').prop("checked", "");
			} 
		}).fail(function() {
			location.href = "LogoutService?timeout=true";
	    });
	}
}
/* END -- Add New Single User functions */


/**
 * Add Multiple New Users functions
 */
function showAddMultipleNewUsersModal() {
	$("#multipleNewUsersTable").html('<thead><tr><th>Name</th><th>Email</th><th>Login</th><th>Password</th><th>Organization</th><th>City</th><th>Country</th></tr></thead>');
	if (!$('#addMultipleNewUsersSaveBtn[disabled]').length) {
		$('#addMultipleNewUsersSaveBtn').attr('disabled', 'disabled');
	}
	$('#addMultipleNewUsersModal').modal('show');	
}
function generateInputFieldsForEachLines () {
	var text = $("#multipleNewUsersTextArea").val();   
	var table = '<thead><tr><th>Name</th><th>Email</th><th>Login</th><th>Password</th><th>Organization</th><th>City</th><th>Country</th></tr></thead><tbody>';
	if (text.trim().length > 0) {
		var lines = text.split(/\r|\r\n|\n/);
		for(var i = 0;i < lines.length;i++){
			var fields = lines[i].split(",");
			if (fields.length == 1) {
				fields = lines[i].split("\t");
			}
			table += '<tr><td><input class="form-control" type="text" onkeyup="amnuNameCheck(this);" value="'+((typeof fields[0] !== 'undefined') ? fields[0].trim(): '')+'"/></td>'+
					'<td><input class="form-control" type="text" onkeyup="amnuEmailCheck(this);" value="'+((typeof fields[1] !== 'undefined') ? fields[1].trim(): '')+'"/></td>'+
					'<td><input class="form-control" type="text" onkeyup="amnuLoginCheck(this);" value="'+((typeof fields[2] !== 'undefined') ? fields[2].trim(): '')+'"/></td>'+
					'<td><input class="form-control" type="text" onkeyup="amnuPasswordCheck(this);" value="'+((typeof fields[3] !== 'undefined') ? fields[3].trim(): '')+'"/></td>'+
					'<td><input class="form-control" type="text" value="'+((typeof fields[4] !== 'undefined') ? fields[4].trim(): '')+'"/></td>'+
					'<td><input class="form-control" type="text" value="'+((typeof fields[5] !== 'undefined') ? fields[5].trim(): '')+'"/></td>'+
					'<td><input class="form-control" type="text" value="'+((typeof fields[6] !== 'undefined') ? fields[6].trim(): '')+'"/></td>'+'</tr>';
		}
		table += '</tbody>';
		$("#multipleNewUsersTable").html(table);
		$("#multipleNewUsersTable input").keyup();
	}
}
function amnuNameCheck (inputField) {
	if ($(inputField).val().length == 0 || $(inputField).val().length > 60) {
		if(!$(inputField).hasClass('error')) {
			$(inputField).addClass('error');
		}
	} else {
		if($(inputField).hasClass('error')) {
			$(inputField).removeClass('error');
		}
	}
	amnuSaveBtnUpdate();
}
function amnuEmailCheck (inputField) {
	if (!reEmail.test($(inputField).val())) {
		if(!$(inputField).hasClass('error')) {
			$(inputField).addClass('error');
		}
	} else {
		if($(inputField).hasClass('error')) {
			$(inputField).removeClass('error');
		}
	}
	amnuSaveBtnUpdate();
}
function amnuPasswordCheck (inputField) {
	if ($(inputField).val().length < 4 || $(inputField).val().length > 60) {
		if(!$(inputField).hasClass('error')) {
			$(inputField).addClass('error');
		}
	} else {
		if($(inputField).hasClass('error')) {
			$(inputField).removeClass('error');
		}
	}
	amnuSaveBtnUpdate();
}
function amnuLoginCheck (inputField) {
	if (!reNotLogPass.test($(inputField).val())) {
		if(!$(inputField).hasClass('error')) {
			$(inputField).addClass('error');
		}
	} else {
		if($(inputField).hasClass('error')) {
			$(inputField).removeClass('error');
		}
	}
	amnuSaveBtnUpdate();
}
function amnuSaveBtnUpdate () {	
	if ($('#multipleNewUsersTable .error').length) {
		$('#addMultipleNewUsersSaveBtn').attr('disabled', 'disabled');
	} else {
		$('#addMultipleNewUsersSaveBtn').removeAttr('disabled');
	}
}
function addMultipleNewUsersSubFunction (callback) {
	var usersTemp = new Array();
	$('#multipleNewUsersTable tbody tr').each(function() {
		var curRow = $(this).children('td');
		usersTemp.push({"name" : ""+curRow.eq(0).children('input').eq(0).val()+"", "email" : ""+curRow.eq(1).children('input').eq(0).val()+"", "login" : ""+curRow.eq(2).children('input').eq(0).val()+"", 
			"password" : ""+curRow.eq(3).children('input').eq(0).val()+"", "organization" : ""+curRow.eq(4).children('input').eq(0).val()+"", "city" : ""+curRow.eq(5).children('input').eq(0).val()+"", 
			"country" : ""+curRow.eq(6).children('input').eq(0).val()+""});
	});
	callback(JSON.stringify(usersTemp));
}
function addMultipleNewUsers () {
	addMultipleNewUsersSubFunction(addMultipleNewUsersAjax);
}
function addMultipleNewUsersAjax (usersJson) {
	var groupMnemonic = $('#groupTitle').attr('mnemonic');
	$.post("addnewuserstothegroup", {g_m: groupMnemonic, u_d: usersJson}, function() {}).done(function(data) {
		var dataTemp = $.parseJSON(data);
		$('#addMultipleNewUsersModal').modal('hide');
		if ('error' in dataTemp) {
			notifyError(dataTemp['error']);
		} else {
			if ('success' in dataTemp) {
				var successObj = dataTemp['success'];
				var addCount = 0;
				for(var i in successObj)
				{
				     var text = successObj[i].success;
				     notificationCenterAddRecord(text, 'success');
				     addCount++;				     
				}
				if (addCount > 0) {
					if (addCount == 1) {
						notifySuccess('1 user was added to the system');
					} else {
						notifySuccess(addCount+' users were added to the system');						
					}
				}
			} 
			if ('exist' in dataTemp) {
				var existObj = dataTemp['exist'];
				var existCount = 0;
				for(var i in existObj)
				{
				     var text = existObj[i].warning;
				     notificationCenterAddRecord(text, 'warning');
				     existCount++;
				}
				if (existCount > 0) {
					if (existCount == 1) {
						notifyDefault('1 user was already in the system');	
					} else {
						notifyDefault(existCount+' users were already in the system');						
					}				
				}
			}
			if ('exception' in dataTemp) {
				var exceptionObj = dataTemp['exception'];
				var sqlCount = 0;
				for(var i in exceptionObj)
				{
					var text = exceptionObj[i].exception;
				    notificationCenterAddRecord(text, 'danger');
					sqlCount++;
				}
				if (sqlCount > 0) {
					if (sqlCount == 1) {
						notifyError('1 user was not added to the system, because of the sql exeption during update');
					} else {
						notifyError(sqlCount+' users were not added to the system, because of the sql exeption during update');					
					}				
				}
			}
			if ('users' in dataTemp) {
				var usersObj = dataTemp['users'];
				addMultipleUsersToGroup(JSON.stringify(usersObj));
			}
		}
	}).fail(function() {
		location.href = "LogoutService?timeout=true";
    });
}
function clearAddMultipleNewUsers () {
	$("#multipleNewUsersTable").html('<thead><tr><th>Name</th><th>Email</th><th>Login</th><th>Password</th><th>Organization</th><th>City</th><th>Country</th></tr></thead>');
	$("#multipleNewUsersTextArea").val('');
	if (!$('#addMultipleNewUsersSaveBtn[disabled]').length) {
		$('#addMultipleNewUsersSaveBtn').attr('disabled', 'disabled');
	}
}
/* END -- Add Multiple New Users functions */
