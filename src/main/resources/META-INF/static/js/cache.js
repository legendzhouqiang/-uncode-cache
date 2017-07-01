$(function () {
    $('#btn-fetch').on('click', function (e) {
    	var ff = $('#input-fetch').val();
    	if(ff.length > 0){
    		$.ajax({
                type: "GET",
                url: "/cache/fetch/" + ff,
                success: function (data) {
                	var tb = $('#fetch');
                    for( var i = 0; i < data.length; i++ ) {
                    	var tr=$("<tr class='success'></tr>");
                    	var td=$("<td>"+data[i].id+"</td>");
                    	td.appendTo(tr);
                    	var td=$("<td>"+data[i].host+"</td>");
                    	td.appendTo(tr);
                    	var td=$("<td>"+data[i].ttl+"</td>");
                    	td.appendTo(tr);
                    	var td=$("<td>"+data[i].level+"</td>");
                    	td.appendTo(tr);
                    	tr.appendTo(tb);
                    }
                }
            });
    	}
    });
    $('#input-search').bind('keypress',function(event){
		clear();
        if(event.keyCode == "13") {  
        	var key = $('#input-search').val();
			if(key.length == 0){
				key = "@all@";
			}
			searchKeys(key);
        }
    });
    $('#btn-delay').on('click', function (e) {
        if ($('#expireHour').val() == "") {
            alert("Please input the number!");
        }
        if($('#expireTime').text()==""){
            $('#updateModal').modal('hide');
            return;
        }
        $.ajax({
            type: 'PUT',
            url: "/cache/" + $('#key').text(),
            data: "hour=" + $('#expireHour').val(),
            success: function (data) {
                show();
                init();
                $('#updateModal').modal('hide');
            }
        });
    });
    init();
});
function clear() {
    $('#local').empty();
    $('#remote').empty();
    $('#keys').empty();
}
function show() {
    $('#alert').show();
    setTimeout(function () {
        $("#alert").hide()
    }, 1000);
}


function delkey(key){
	 $.ajax({
         type: "DELETE",
         url: "/cache/remove/" + key,
         success: function (data) {
             show();
             init();
         }
     });
}

function getkey(key){
	  $.ajax({
        type: "GET",
        url: "/cache/key/" + key,
        success: function (data) {
        	var local = data['local'];
            var remote = data['remote'];
            try {
            	local = eval("(" + local + ")");
            	remote = eval("(" + remote + ")");
            } catch (e) {
                $('#local').empty();
                $('#local').prepend(local);
                $('#remote').empty();
                $('#remote').prepend(remote);
                return;
            }
            if (remote instanceof Array) {
                if (remote.length > 300) {
                    $('#remote').empty();
                    $('#local').empty();
                    $('#remote')
                        .prepend('<p>Too huge to preivew</p>');
                    $('#local')
                    .prepend('<p>Too huge to preivew</p>');
                } else {
                    $("#remote").JSONView(remote);
                    $('#local').JSONView(local);
                }
            } else if (typeof(remote) == "object"
                && Object.prototype.toString
                    .call(remote).toLowerCase() == "[object object]"
                && !remote.length) {
                $("#remote").JSONView(remote);
                $("#local").JSONView(local);
            } else {
                $('#remote').empty();
                $('#remote').prepend(remote);
                $('#local').empty();
                $('#local').prepend(local);
            }
        }
    });
}

function searchKeys(keys){
	$.ajax({
        type: "GET",
        url: "/cache/keys/" + keys,
        success: function (defaultData) {
            var tb = $('#keys');
            for( var i = 0; i < defaultData.length; i++ ) {
            	var tr=$("<tr></tr>");
            	var td=$("<td width='85%' style='overflow:hidden;text-overflow:ellipsis;'></td>");
            	var a=$("<a href='javascript:void(0);' title='" + defaultData[i] + "' onclick=getkey('"+defaultData[i]+"')>"+ defaultData[i] +"</a>");
            	a.appendTo(td);
            	td.appendTo(tr);
            	var td=$("<td><a href='javascript:void(0);' onclick=delkey('"+defaultData[i]+"')>删除</a></td>");
            	td.appendTo(tr);
            	tr.appendTo(tb);
            }
        }
    });
}

function init() {
    clear();
    searchKeys("@all@");
}