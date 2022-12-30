let userLogInEmail;
let userLogInId;
let peoples;
let router;
let listOfExpenses;
let listOfPaymentRequest;
const zarFormat = new Intl.NumberFormat("en-US",{style:"currency",currency:"ZAR"});

function matchPaymentRequestWithExpense(id){
  let matchedExpenses = [];
  for(let index = 0; index < listOfPaymentRequest.length; index++){
      if(listOfPaymentRequest[index].expenseId==id){
        if(listOfPaymentRequest[index]){
        matchedExpenses.push(listOfPaymentRequest[index]);
        }
      }
  }
  
  return matchedExpenses;
}

function getAllPaymentRequest(){
  const options = {
    method: 'GET'
  };
  fetch(`http://localhost:5050/paymentrequests`, options)
        .then(response => response.json())
        .then(data => {
         listOfPaymentRequest = data;
        })    
}

function getUserByEmail(email){
  for(let index = 0; index < peoples.length; index++){
    if(peoples[index].email == email){
       const useid = peoples[index].id; 
       return useid;
    }
  }
}

function getAllPeople(){
  const options = {
    method: 'GET'
  };
  fetch(`http://localhost:5050/people`, options)
        .then(response => response.json())
        .then(data => {
         peoples = data;
        })    
}

function getAllExpenses(){
  const options = {
    method: 'GET'
  };
  fetch(`http://localhost:5050/expenses`, options)
        .then(response => response.json())
        .then(data => {
         listOfExpenses = data;
        })    
}




function expenses(id){
  userLogInId = id;
  const options = {
      method: 'GET'
  };
  
  fetch(`http://localhost:5050/expenses/person/${id}`, options)
          .then(response => response.json())
          .then(data => {
      
            
            const expenses = {
              expenses:data,
              email:userLogInEmail
            }
          
            const template = document.getElementById('expenses-template').innerText;
            const compiledFunction = Handlebars.compile(template);
            document.getElementById('app').innerHTML = compiledFunction(expenses);
            router.redirectTo("/expenses");
            
          });

}

function expensesById(id){
  const options = {
      method: 'GET'
  };

  fetch(`http://localhost:5050/expenses/${id}`, options)
          .then(response => response.json())
          .then(data => {
            const expense = {
              expenses:data,
              email:userLogInEmail,
              payrequestForExpense:matchPaymentRequestWithExpense(id)
            }
          
            const template = document.getElementById('paymentrequest-template').innerText;
            const compiledFunction = Handlebars.compile(template);
            document.getElementById('app').innerHTML = compiledFunction(expense);
          });

}

function paymentrequests_sent(){
  const options = {
      method: 'GET'
  };

  fetch(`http://localhost:5050/paymentrequests/sent/${userLogInId}`, options)
          .then(response => response.json())
          .then(data => {
            let filtedPayment = [];
            for(var index = 0; index < data.length;index++){
               if(!data[index].isPaid){
                  filtedPayment.push(data[index]);
               }
            }
           
            const sentPayment = {
              sentPaymentRequest:filtedPayment,
              email:userLogInEmail,
              peoples:peoples,
              listOfExpenses:listOfExpenses

            }

            const template = document.getElementById('paymentrequests_sent-template').innerText;
            const compiledFunction = Handlebars.compile(template);
            document.getElementById('app').innerHTML = compiledFunction(sentPayment);
          });
}
  

// TO DO add paymentrequests_received() function

function paymentrequests_overdue(){
  const options = {
      method: 'GET'
  };

  fetch(`http://localhost:5050/paymentrequests/sent/${userLogInId}`, options)
          .then(response => response.json())
          .then(data => {
            var nowdate = new Date();
            var peopleDue = [];
            for(let index = 0; index < data.length;index++){
              
              var newdate = data[index].date.split("/")
              var day_start = new Date(`${nowdate.getMonth()+1} ${nowdate.getDate()} ${nowdate.getFullYear()}`);
              var day_end = new Date(`${newdate[1]} ${newdate[0]} ${newdate[2]}`);
              var days = (day_end - day_start) / (1000 * 60 * 60 * 24);
              
              if(!data[index].isPaid && days < 0){
                peopleDue.push(data[index]);
              }
            }
             return peopleDue;
            })
          .then(data1 => {
            const sentPayment = {
              sentPaymentRequest:data1,
              email:userLogInEmail,
              peoples:peoples,
              listOfExpenses:listOfExpenses
            }
          
            const template = document.getElementById('overdue_paymentrequests-template').innerText;
            const compiledFunction = Handlebars.compile(template);
            document.getElementById('app').innerHTML = compiledFunction(sentPayment);
          });
}

function paymentRequestReceived(){
  const options = {
      method: 'GET'
  };

  fetch(`http://localhost:5050/paymentrequests/received/${userLogInId}`, options)
          .then(response => response.json())
          .then(data => {
            const paymentRequestReceived = {
              paymentRequestReceived:data,
              email:userLogInEmail
            }
            console.log(paymentRequestReceived)
            const template = document.getElementById('paymentRequestReceived-template').innerText;
            const compiledFunction = Handlebars.compile(template);
            document.getElementById('app').innerHTML = compiledFunction(paymentRequestReceived);

          });
}
function login(){
 
    const form = document.getElementById("login-form");
    form.addEventListener("submit", (event) => {
        event.preventDefault();

        const data = new FormData(event.target);
        const email = data.get("email");
        userLogInEmail = email;
        const jsonBody = {
          "email":email
        }

    const options = {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(jsonBody)
    };

    fetch(`http://localhost:5050/people`, options)
            .then(response => response.json())
            .then(data => {
                data = {
                    email: data.email,
                    id: data.id
                };
                expenses(data.id);
            });
    });;
}


window.addEventListener('load', () => {
  const app = $('#app');
  getAllPeople();
  getAllExpenses();
  getAllPaymentRequest();
 

  const defaultTemplate = Handlebars.compile($('#default-template').html());
  Handlebars.registerPartial("navPartial",$("#nav-template").html());
  const expensesTemplate = Handlebars.compile($('#expenses-template').html());
  const newexpense_Template = Handlebars.compile($('#newexpense-tamplate').html());
  const paymentrequests_sentTemplate = Handlebars.compile($('#paymentrequests_sent-template').html());

 

  Handlebars.registerHelper("getPeopleName", function(peopleId){
    for(let index = 0; index < peoples.length; index++){
      if(peoples[index].id === peopleId){
         const email = peoples[index].email;
         return email.split("@")[0];
      }
    }
  })

  Handlebars.registerHelper("getNameFromEmail", function(email){
         if(email){
           return email.split("@")[0];
         }
         
  })

  Handlebars.registerHelper("getExpenseDescription", function(expenseId){
   
    for(let index = 0; index < listOfExpenses.length; index++){
      if(listOfExpenses[index].expenseId === expenseId){
        return listOfExpenses[index].description;
      }
    }
  })

  Handlebars.registerHelper("zarformatter", function(money){
    return zarFormat.format(money);
  })

  Handlebars.registerHelper("yesOrNo", function(booleanType){
    if(booleanType){
      return "Yes";
    }else{
      return "No";
    }
  })

  Handlebars.registerHelper("dueDate", function(duedate){
    var nowdate = new Date();
    var newdate = duedate.split("/")
   
    var day_start = new Date(`${nowdate.getMonth()+1} ${nowdate.getDate()} ${nowdate.getFullYear()}`);
    var day_end = new Date(`${newdate[1]} ${newdate[0]} ${newdate[2]}`);
    var total_days = (day_end - day_start) / (1000 * 60 * 60 * 24);
    return total_days <= 0 ? 0 : total_days;
  })

  Handlebars.registerHelper("totalExpense", function(myExpenses){ 
    let myTotalExpense = 0;
    for(var index = 0; index < myExpenses.length;index++){
       myTotalExpense = myTotalExpense + myExpenses[index].nettAmount;
    }
    return zarFormat.format(myTotalExpense);
  })


  Handlebars.registerHelper("totalUnpaid", function(myExpenses){
    let unpaidExpense = 0;
    for(var index = 0; index < myExpenses.length;index++){
       if(!myExpenses[index].isPaid){
          unpaidExpense = unpaidExpense + myExpenses[index].amount;
       }
    }
    return zarFormat.format(unpaidExpense);
  })

  Handlebars.registerHelper("paymentRequestsUnpaid", function(paymentRequestsForExpense){
    let unpaidRequest = 0;
    for(var index = 0; index < paymentRequestsForExpense.length;index++){
       if(!paymentRequestsForExpense[index].isPaid){
          unpaidRequest = unpaidRequest + paymentRequestsForExpense[index].amount;
       }
     
    }
    return zarFormat.format(unpaidRequest);
  })

  Handlebars.registerHelper("toBePayByYou", function(paymentRequestsForExpense,expenseAmount){
    let unpaidRequest = 0;
    for(var index = 0; index < paymentRequestsForExpense.length;index++){
       if(!paymentRequestsForExpense[index].isPaid){
          unpaidRequest = unpaidRequest + paymentRequestsForExpense[index].amount;
       }
     
    }
    return zarFormat.format(expenseAmount - unpaidRequest);
  })

 

  router = new Router({
    mode:'hash',
    root:'index.html',
    page404: (path) => {
      const html = defaultTemplate();
      app.html(html);
    }
  });
  
  router.add('/expenses', async () => {
    const html = expensesTemplate();
    app.html(html);
    expenses(userLogInId);
  });

  router.add('/overdue',  function() {
    paymentrequests_overdue();
  });


  router.add('/paymentrequests_sent', async () => {
      const html = paymentrequests_sentTemplate();
      app.html(html);
      paymentrequests_sent();
    });
  

  // TO DO /paymentrequests_received route handler{

 router.add('/paymentrequests_received', async () => {
       paymentRequestReceived();
     });
  

  router.add('/newexpense',  function() {
    const html = newexpense_Template();
    app.html(html);

  });

  router.add('/paymentrequest/{expenseid}',  async(expenseid) => {
    expensesById(expenseid);
  });
  
  router.add('/login',  async() => {
    const html = defaultTemplate();
    app.html(html);
    login();
    
  });

  router.add('/logout',  async() => {
    const html = defaultTemplate();
    app.html(html);
    router.navigateTo('/login');
  });



  router.addUriListener();
 
  window.addEventListener("submit",(event)=>{
    let form = document.getElementById("form-paymentrequest");
    if(form){

      event.preventDefault();
      const data = new FormData(event.target);

      const email = data.get("email");
      const date = data.get("date");
      const amount = data.get("amount");
      const expenseId = data.get("expenseId");

      const dateFormate = date.split("-")
     
      const jsonBody = {
        expenseId:expenseId,
        fromPersonId:userLogInId,
        toPersonId:getUserByEmail(email),
        date:`${dateFormate[2]}/${dateFormate[1]}/${dateFormate[0]}`,
        amount:amount
      }
      

      const options = {
        method: 'POST',
        headers: {
        'Content-Type': 'application/json'
         },
      body: JSON.stringify(jsonBody)
      };

      fetch(`http://localhost:5050/paymentrequests`, options)
          .then(response => response.json())
          .then(data => {
            const expenses = {
              expenses:data,
              email:userLogInEmail
            }

            getAllPeople();
            getAllExpenses();
            getAllPaymentRequest();
            router.navigateTo('/paymentrequests_sent');
          
          });

   }
 
   
   form = document.getElementById("expense-form");
   if(form){
    event.preventDefault();
    const data = new FormData(event.target);
    const desc = data.get("description");
    const date = data.get("date");
    const amount = data.get("amount");
    const dateFormate = date.split("-")
   

    const jsonBody = {
      personId:userLogInId,
      description:desc,
      date:`${dateFormate[2]}/${dateFormate[1]}/${dateFormate[0]}`,
      amount:amount
    }

    const options = {
      method: 'POST',
      headers: {
      'Content-Type': 'application/json'
       },
    body: JSON.stringify(jsonBody)
    };

    fetch(`http://localhost:5050/expenses`, options)
        .then(response => response.json())
        .then(data => {
          const expenses = {
            expenses:data,
            email:userLogInEmail
          }

          getAllPeople();
          getAllExpenses();
          getAllPaymentRequest();
          router.navigateTo('/expenses');
        
        });

  }

  //TO DO payment form handler
  form = document.getElementById("pay-form");
     if(form){
      event.preventDefault();
      const data = new FormData(event.target);
      const expenseId = data.get("expenseId");
      const requestId = data.get("requestId");


      const jsonBody = {
        expenseId:expenseId,
        paymentRequestId:requestId,
        payingPersonId:userLogInId
      }

      const options = {
        method: 'POST',
        headers: {
        'Content-Type': 'application/json'
         },
      body: JSON.stringify(jsonBody)
      };

      fetch(`http://localhost:5050/payments`, options)
          .then(response => response.json())
          .then(data => {
            const expenses = {
              expenses:data,
              email:userLogInEmail
            }

            getAllPeople();
            getAllExpenses();
            getAllPaymentRequest();
            router.navigateTo('/expenses');

          });

    }

  form = document.getElementById("recall-form");
  if(form){
   event.preventDefault();
   const data = new FormData(event.target);
   const requestId = data.get("requestId");
  
   const options = {
     method: 'DELETE',
 
   };

   fetch(`http://localhost:5050/paymentrequests/${requestId}`, options)
       .then(response => {

         getAllPeople();
         getAllExpenses();
         getAllPaymentRequest();
         router.navigateTo('/overdue');
       
       });

 }


  })
 
  $('a').on('click', (event) => {
    event.preventDefault();
    const target = $(event.target);
    const href = target.attr('href');
    const path = href.substring(href.lastIndexOf('/'));
    router.navigateTo(path);
  });
  
 
  router.navigateTo('/login');
});
