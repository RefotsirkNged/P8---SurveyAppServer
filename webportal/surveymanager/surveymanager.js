var selectedRowIndex = -1;

function getQuestions() {
    var g = document.getElementById("questiontable");
    var surveyId = 4091;//sessionStorage.getItem("questiontable");
    g.innerHTML = "";

    $.ajax({
        url: 'http://localhost:8081/api/survey/' + surveyId + '/object',
        type: 'GET',
        beforeSend: function (request) {
            request.setRequestHeader("token", sessionStorage.getItem("token"));
        },
        success: function (response) {
            response.questions.forEach(function (value) {
                g.innerHTML +=  "<tr>\n" +
                    "<td>\n" + value.title + " " +
                    " <button id='removequestionbuttom" + value.id + "' type=\"button\" class=\"btn btn-danger btn-sm\" onclick=\"removeQuestion(" + value.id + ")\" style='float: right;'>-</button>\n" +
                    "</td>\n" +
                    "</tr>";
            });
        }
    })
}

function removeQuestion(questionId) {
    if(confirm("Er du sikker på at du vil slette spørgsmålet?")) {
        var surveyId = 4091;//sessionStorage.getItem("questiontable");

        $.ajax({
            url: 'http://localhost:8081/api/survey/' + surveyId + '/question/' + questionId,
            type: 'DELETE',
            beforeSend: function (request) {
                request.setRequestHeader("token", sessionStorage.getItem("token"));
            },
            success: function (response) {
                getQuestions();
            }
        })
    }
}

function pickInputTypeDiv() {
    var dropdown = document.getElementById("questionInputTypeInput");

    document.getElementById("textDiv").style.display = "none";
    document.getElementById("numberDiv").style.display = "none";
    document.getElementById("dropdownDiv").style.display = "none";

    if (dropdown.selectedIndex == 1) {
        document.getElementById("textDiv").style.display = "block";
    }
    else if (dropdown.selectedIndex == 2) {
            document.getElementById("numberDiv").style.display = "block";
    }
    else if (dropdown.selectedIndex == 3) {
                document.getElementById("dropdownDiv").style.display = "block";
    }

}

function dropdownValueClicked(row) {
    var table = document.getElementById("dropdownValuesTable");
    for (i = 0; i < table.rows.length; i++) {
        table.rows[i].classList.remove("table-active");
    }

    selectedRowIndex = row.rowIndex;
    row.classList.add("table-active");
}