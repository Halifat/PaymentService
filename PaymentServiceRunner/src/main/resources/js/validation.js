function validationCardNumber(num){
    if( num.value.match(/\w{16}/i) && num.value.length==16) {
        console.log('yeah');
        num.setCustomValidity('');
        return true;
    }else{
        num.setCustomValidity('Неверный номер карты');  
        console.log('nope');
        return false;
    }
}
function validationValue(val){
    if(val.value.match(/[0-9]{1,8}\.[0-9]{2}/i)){
        console.log('yeah');
        val.setCustomValidity('');
        return true;
    }else{
        val.setCustomValidity('Неверный формат денежной суммы'); 
        console.log('nope');
        return false;
    }
}
function validationCode(code){
    if(code.value.match(/[0-9]{3}/i) && code.value.length==3){
        console.log('yeah');
        code.setCustomValidity('');
        return true;
    }else{
        code.setCustomValidity('Неверный формат кода безопасности(3 цифры)'); 
        console.log('nope');
        return false;
    }
}
function validationDate(date){
    if(date.value.match(/[0-9][0-9]\/[0-9][0-9]/i) && date.value.length==5){
        let d_arr = date.value.split('\/');
        
        let mounth=new Number(d_arr[0]);
        let year=new Number(d_arr[1]);
        if(mounth<=12 && year<=22 &&year>17){
            console.log('yeah');
            date.setCustomValidity('');
            return true;
        }  else{
            date.setCustomValidity('Неверный формат даты(MM/YY)'); 
            console.log('nope');
            return false;
        }
    }else{
        console.log('nope');
        date.setCustomValidity('Неверный формат даты(MM/YY)'); 
        return false;
    }
  }
function validationName(name){
    if(name.value.match(/^\S{3,20} \S{3,20}$/i)){
        console.log('yeah');
        name.setCustomValidity(''); 
        return true;
    }else{
        name.setCustomValidity('Неверный формат имени владельца'); 
        console.log('nope');
        return false;
    }
}
function submitForm(){
    let number = document.getElementsByName('number')[0];
    let toNumber=document.getElementsByName('toNumber')[0];
    let value=document.getElementsByName('value')[0];
    let date=document.getElementsByName('date')[0];
    let currency=document.getElementsByName('currency')[0];
    let code=document.getElementsByName('code')[0];
    let name= document.getElementsByName('name')[0];
    console.log(number.value+';'+toNumber.value+';'+value.value+';'+date.value+';'+currency.value+';'+code.value+';'+name.value);
    if(validationCardNumber(number) 
    && validationCardNumber(toNumber)
    && validationCode(code)
    && validationDate(date)
    && validationValue(value)
    && validationName(name)){
        console.log('ok');
        let req={
            fromAccount:number.value.toUpperCase(),
            toAccount:toNumber.value.toUpperCase(),
            value:value.value,
            date:date.value,
            currency:currency.value,
            code:code.value,
            name:name.value.toUpperCase()
        };

        $.ajax({
            url: 'operation',
            type: 'POST',
            dataType: 'json',
            data: JSON.stringify(req),
            contentType: 'application/json',
            mimeType: 'application/json',   
            success: function (data) {
            	console.log(data);
                document.getElementById('offert').innerHTML='<h3>'+data.message+'</h3>';
            },
            error:function(data,status,er) {
            	console.log(data.responseJSON);
                document.getElementById('offert').innerHTML='<p>'+data.responseJSON.message+'</p>';
                
            }
        });
    }
}

