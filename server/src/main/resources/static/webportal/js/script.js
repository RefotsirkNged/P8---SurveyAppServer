function arr_diff (a1, a2) {
    var diff = [];

    a1.forEach(function(e1){
        var exists = false;

        a2.forEach(function(e2){
            if(e1.id === e2.id) {
                exists = true;
            }
        });

        if(!exists) {
            diff.push(e1);
        }
    });

    return diff;
}