(function(KT, _$) {
    
    KT.$ = _$;
    
    var localizedStrings={
        save:"Save",
        edit:"Edit",
        add:"+",
        remove:"-",
        cancel:"Cancel",
        errors:{
            'null.table':'Table is null',
            'null.row':'Row is null',
            'null.cell':'Cell is null',
            'null.columns':'Column array is null or zero length',
            'null.rows':'Row array is null or zero length',
            'commit.noedits':'No edits to commit'
        }
    };
    
    KT.setEditable = function(table) {
        KT.setEditable(table, false);
    }
    
    KT.setEditable = function(table, fixed) {
        
        if (table == null) {
            alert(localizedStrings['errors']['null.table']);
        } 
        
        var columns = table.data("columnModel");
        
        if (columns == null) {
            alert(localizedStrings['errors']['null.columns']);
            return;
        }
        
        var columnCount = columns.getColumnCount();
        
        var saveText = localizedStrings['save'];
        
        var row = table.find("tr:last");
        
        row.after("<tr></tr>");
        
        row = table.find("tr:last");
        
        row.html('<td></td>');
        
        var control = row.find("td:last");
        
        control.addClass("control");
        
        if (!fixed) {
            
            control.attr('colspan',columnCount+1);
            
            var addText = localizedStrings['add'];
            
            control.append( "<input class=\"addButton\" type=\"button\" value=\""+addText+"\"/>");
            
            var bAdd = control.find("input.addButton");
            
            
        
            bAdd.click(function(ev){
            
                var row = KT.addRow(table);
            
                if (row != null) {
                    row.data('created',true);
                }
            
                row.data('id', '-1');
        
            });
            
        } else {
            
            control.attr('colspan',columnCount);
                        
            table.find("tr").each(function(i) {
                
                var row = $(this);
                
                if (i%2 == 0) {
                    row.addClass("even");
                } else {
                    row.addClass("odd");
                }
                
                row.find("td").each(function(i) {
                
                    var cell = $(this);
                
                    if (cell.hasClass("control")) {
                        return;
                    }
                
                    var name = columns.getName(i);
                
                    cell.data("column", name);
                
                    if (columns.isEditable(i)) {
                        cell.addClass("editable");
                        
                        cell.bind('click',KT.editCell);
                    }
            
                });
                
                
            
            
            });
        }
        
        control.append("<input class=\"saveButton\" type=\"button\" value=\""+saveText+"\"/>");
        
        var bSave = control.find("input.saveButton");
        
        bSave.click(KT.onSaveTable);
        
    }
    
    KT.configureTable = function(table) {
        
        if (table == null) {
            alert(localizedStrings['errors']['null.table']);
        } 
        
        var columns = table.data('columnModel');
        
        if (columns == null) {
            alert(localizedStrings['errors']['null.columns']);
            return;
        }
       
        var html = table.html();
        
        html+=("<thead><tr></tr></thead>");
        
        table.html(html);
        
        var row = table.find("tr");
        
        html = row.html();
        
        for (var i=0;i<columns.getColumnCount();i++) {
            
            html+="<th>";
            html+=columns.getTitle(i);
            html+="</th>";
        
        }
        
        html+="<th>&nbsp;</th>";
        
        row.html(html);
        
        KT.setEditable(table);
    
    }
   
    KT.onSaveTable = function(ev) {
        
        ev.preventDefault();
        
        KT.commitTable($(this).closest("table"));
    
    }
    
    KT.onDeleteClick = function(ev) {
        
        ev.preventDefault();
        
        var row = $(this).closest("tr");
        
        KT.deleteRow(row);
    
    }
    
    KT.onEditClick = function(ev) {
        
        ev.preventDefault();
        
        var row = $(this).closest("tr");
        
        if (KT.editRow(row)) {
            
            $(this).unbind('click');
        
            $(this).click(KT.onCommitClick);
        
            $(this).val(localizedStrings['save']);
            
        }
    
    }
    
    KT.onCommitClick = function(ev) {
        
        ev.preventDefault();
        
        $(this).unbind('click');
        
        var row = $(this).closest("tr");
        
        KT.commitRow(row);
        
        $(this).click(KT.onEditClick);
        
        $(this).val(localizedStrings['edit']);
    
    }
    
    KT.getColumnCount = function(tableIndex) {
        
        var table = $(tableIndex);
        
        if (table == null) {
            alert(localizedStrings['errors']['null.table']);
        } 
        
        var count = table.find(" tr:first > th").size();
        
        if (count == 0) {
            return table.find(" tr:first > td").size();
        }
        
        return count;
    
    }
        
    KT.addRow = function(table) {
        
        if (table == null) {
            alert(localizedStrings['errors']['null.table']);
            return null;
        } 
        
        var columns = table.data('columnModel');
        
        var removeText = localizedStrings['remove'];
        var editText = localizedStrings['edit'];
        
        var lastRow = table.find("tr:last");
        
        var html = "<tr></tr>";
        
        lastRow.before(html);
        
        var row = lastRow.prev();
        
        if (row.index() % 2 == 0) {
            row.addClass("even");
        } else {
            row.addClass("odd");
        }
        
        var cell;
        
        for (var i =0;i< columns.getColumnCount();i++) {
            
            row.append('<td name="'+i+'"></td>');
            
            cell = row.find("td:last");
            
            cell.data("column", i);
            
            if (columns.isEditable(i)) {
                cell.addClass("editable");
            }
            
            row.find("td[column="+i+"]").html("gigi");
            
        }
        
        row.append("<td></td>");
        
        cell = row.find("td:last");
        
        cell.append('<input class="deleteRow" type="button" value="'+removeText+'" />');
        cell.append('<input class="editRow" type="button" value="'+editText+'" />');
        
        var button = cell.find(".deleteRow");
        
        button.click(KT.onDeleteClick);
        
        button.button();
        
        button = cell.find(".editRow");
        
        button.click(KT.onEditClick);
        
        button.button();
        
        return row;
    
    }
    
    KT.deleteRow = function(row) {
        
        if (row == null) {
            alert(localizedStrings['errors']['null.row']);
            return;
        }
        
        var created = row.data('created');
        
        if (created) {
            // rimuovo la linea che non è nel db
            row.remove();
        } else {
            // invisibilizzo la riga in modo da poterla cancellare dal db
            row.data('removed',true);
            row.addClass('removed');
        }
    
    }
    
    KT.editRow = function(row) {
        
        if (row == null) {
            alert(localizedStrings['errors']['null.row']);
            return false ;
        }
        
        var table = row.closest("table");
        
        if (table.find("td.editing").length > 0) {
            alert("You are editing another row, please commit and try again please.");
            return false;
        }
        
        row.find("td").each(function(i) {
            
            var cell = row.find("td:nth-child("+i+")");
            
            if (cell != null && cell.hasClass("editable")) {
                
                cell.removeClass("editable");
                
                cell.addClass("editing");
                
                var text = cell.html();
                
                cell.html('<input class="celleditor" type="text" value="'+text+'"/>');
                
                
            }
        
        });
        
        $(":button").button();
        
        return true;
    
    }
    
    KT.editCell = function(ev) {
        
        ev.preventDefault();
        
        var cell = $(this);
        
        if (cell == null){
            alert(localizedStrings['errors']['null.cell']);
            return false;
        }
        
        if (!cell.hasClass("editable").length > 0) {
            return false;
        }
        
                
        var table = cell.closest("table");
        
        if (table.find("td.editing")) {
            alert("cell:already editing");
            return false;
        }
        
        cell.removeClass("editable");
                
        cell.addClass("editing");
                
        var text = cell.html();
                
        cell.html('<input class="celleditor" type="text" value="'+text+'"/>');
        
        var editor = cell.find("input[type=text]");
        
        editor.bind('blur', KT.commitCell);
        
        return true;
        
    }
    
    KT.commitCell = function(ev) {
        
        var cell = $(this).closest("td");
            
        cell.html(cell.find("input").val());
            
        cell.removeClass("editing");
        cell.addClass("editable");
            
        KT.setRowUpdated( cell.closest("tr"));
    }
    
    KT.setRowUpdated = function(row) {
        if (!row.data('created')) {
            row.data('updated', true);
        }
    }
    
    KT.commitRow = function(row) {
        
        if (row == null) {
            alert(localizedStrings['errors']['null.row']);
            return;
        }
        
        row.find("td").each(function(i) {
            
            var cell  = row.find("td:nth-child("+i+")");
            
            if (cell != null && cell.hasClass("editing")) {
                
                var text = cell.find("input").val();
                
                cell.html(text);
                
                cell.removeClass("editing");
                
                cell.addClass("editable");
            
            }
        });
        
        KT.setRowUpdated(row);
    
    }
        
    KT.commitTable  = function(table) {
        
        if (table == null) {
            alert(localizedStrings['errors']['null.table']);
            return;
        }
        
        if (table.find("td.editing").length > 0) {
            alert("Commit any edits before saving, please");
            return;
        }
        
        
        table.find("tr").each(function(index) {
            
            //@todo ajax this
            
            var row = $(this);
            
            if (row.data('created')) {
                row.data('created',false);
                row.data('id', Math.floor(Math.random()*100));//@todo remove this
                alert("create row "+index+":"+JSON.stringify(KT.marshall(row)))
            } else if (row.data('updated')) {
                row.data('updated',false);
                alert("update row "+index+":"+JSON.stringify(KT.marshall(row)))
            } else if (row.data('removed')) {
                alert("delete row "+index+":"+JSON.stringify(KT.marshall(row)))
                row.remove();
            }
        
        });
    
    
    }
    
    KT.marshall = function(row) {
        
        var table = row.closest("table");
        
        var columns = table.data('columnModel');
        
        var json = {};
        
        for (var i =0;i< columns.getColumnCount();i++) {
            
            var cell = row.find("td:nth-child("+i+")");

            if (cell != null) {
                json[i] = cell.html();
            } else {
                alert("cannot find column "+i);
            }
            
        }
        
        return json;
    }
    
    KT.unmarshall = function(table, json) {
        
        var columns = table.data('columnModel');
        
        var row = addRow(table);
        
        row.data('id') = json['id'];
        
        row.find("td").each(function(i) {
            
            var cell  = row.find("td:nth-child("+i+")");
            
            if (cell.hasClass("editable")) {
                
                cell.html(json[columns.getName(i)]);
            
            }
        
        });
    
    }

} (window.KT = window.KT || {}, jQuery) );

function ColumnModel() {
        
    this.DATA_PROPERTY_NAME = "columnModel";
    this.columns = [];
        
    this.addColumn = function(name) {
        this.addColumn(name, name);
    }
        
    this.addColumn= function(name, title){
        this.addColumn(name, title, false);
    }
        
    this.addColumn= function(name, title, editable) {
        this.columns.push({
            "name":name,
            "title":title,
            "editable":editable
        })
    }
        
    this.getColumnCount= function() {
        return this.columns.length;
    }
        
    this.getName= function(index) {
        if (index <0 || index >= this.columns.length) {
            alert("index out of bounds "+index);
            return null;
        }
        return this.columns[index]["name"];
    }
        
    this.getTitle= function(index) {
        if (index <0 || index >= this.columns.length) {
            alert("index out of bounds "+index);
            return null;
        }
        return this.columns[index]["title"];
    }
        
    this.isEditable= function(index) {
        if (index <0 || index >= this.columns.length) {
            alert("index out of bounds "+index);
            return false;
        }
        return this.columns[index]["editable"];
    }
}

