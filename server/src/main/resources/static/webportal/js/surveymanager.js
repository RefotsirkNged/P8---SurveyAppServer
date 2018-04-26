var selectedRowIndex = -1;

function getQuestions() {
    console.log("HEJ")
    var g = document.getElementById("questiontable");
    var surveyId = sessionStorage.getItem("surveyid");
    g.innerHTML = "";

    $.ajax({
        url: 'http://localhost:8081/api/survey/' + surveyId + '/object',
        type: 'GET',
        beforeSend: function (request) {
            request.setRequestHeader("token", sessionStorage.getItem("token"));
        },
        success: function (response) {
            response.questions.forEach(function (value) {
                g.innerHTML += "<tr>\n" +
                    "<td>\n" + value.title + " " +
                    " <button id='removequestionbuttom" + value.id + "' type=\"button\" class=\"btn btn-danger btn-sm\" onclick=\"removeQuestion(" + value.id + ")\" style='float: right;'>-</button>\n" +
                    "</td>\n" +
                    "</tr>";
            });
        }
    })
}

function removeQuestion(questionId) {
    if (confirm("Er du sikker på at du vil slette spørgsmålet?")) {
        var surveyId = sessionStorage.getItem("surveyid");

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

    if (dropdown.selectedIndex === 1) {
        document.getElementById("textDiv").style.display = "block";
    }
    else if (dropdown.selectedIndex === 2) {
        document.getElementById("numberDiv").style.display = "block";
    }
    else if (dropdown.selectedIndex === 3) {
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

function addEmptySurvey() {
    var surveyId;
    $.ajax({
        url: 'http://localhost:8081/api/survey',
        type: 'POST',
        success: function (response) {
            console.log("response " + response);
            sessionStorage.setItem("surveyid", response);
        },
        error: function (response) {
            window.location.replace("../home/index.html");
        }
    });
}

function load() {
    if (sessionStorage.getItem("surveyid") != null && sessionStorage.getItem("surveyid") !== "undefined") {
        getQuestions();
    } else {
        addEmptySurvey();
    }
}

function addQuestion() {
    var name = document.getElementById("questionNameInput").value;
    var description = document.getElementById("questionDescriptionInput").value;
    var index = document.getElementById("questionInputTypeInput").selectedIndex;
    var inputtype;
    var type = "STRING";
    var values = [];

    if (index === 1) {
        inputtype = "TEXT";
    } else if (index === 2) {
        inputtype = "NUMBER";
    } else if (index === 3) {
        inputtype = "DROPDOWN";
        var tbl = document.getElementById("dropdownValuesTable");
        for (var i = 0, row; row = tbl.rows[i]; i++) {
            for (var j = 0, col; col = row.cells[j]; j++) {
                values.push(col.innerHTML);
            }
        }
        //  document.getElementById("dropdownValuesTable").getElementsByTagName("tr").forEach(function (value) {
        //    values.push(value.getElementsByTagName("td")[0].value);
        //    });
    }

    var json = {};
    json.title = name;
    json.description = description;
    json.input = inputtype;
    json.type = type;
    json.values = values;

    console.log(json);

    $.ajax({
        url: 'http://localhost:8081/api/survey/' + sessionStorage.getItem("surveyid") + "/question",
        type: 'POST',
        data: JSON.stringify(json),
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        success: function (response) {
            console.log("response" + response);
            getQuestions();
        },
        error: function (error) {
            console.log("error" + JSON.stringify(error));
        }
    });
}

function removeValueFromTable() {
    document.getElementById("dropdownValuesTable").deleteRow(selectedRowIndex);
}

function addValueToTable() {
    $('#dropdownValuesTable').append('<tr onclick="dropdownValueClicked(this)">\n' +
        '    <td>' + document.getElementById("dropdownValueText").value + '</td>\n' +
        '</tr>');
}

function updateSurveyMetadata() {

}