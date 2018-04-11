function getGroupMembers(groupid) {
    var g = document.getElementById("groupMemberBody");
    var groupid = 0;
    g.innerHTML = "";

    $.ajax({
        url: 'http://localhost:8081/api/group/' + groupid + '/surveys/',
        type: 'GET',
        beforeSend: function (request) {
            request.setRequestHeader("token", sessionStorage.getItem("token"));
            request.setRequestHeader("groupID", groupid);
        },
        success: function (response) {
            response.members.forEach(function (value) {
                g.innerHTML +=  "<tr>\n" +
                    "<td>\n" + value.title + " " +
                    " <button id='removequestionbuttom" + value.id + "' type=\"button\" class=\"btn-danger btnMemberList\" onclick=\"removeMemberFromGroup(" + value.id + "," + marked.id +")\" style='float: right;'>-</button>\n" +
                    "</td>\n" +
                    "</tr>";

                membersInSelectedGroup.push(value.id);
            });

            membersInSelectedGroup.forEach(function (value) {
                document.getElementById("addparbtn" + value).hidden = true;
            });
        }
    })
}