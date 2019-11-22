
document.addEventListener('DOMContentLoaded', () => {
    const view = document.querySelector('.view');
    // const quests = document.querySelector('.quest');

    const quests = document.querySelectorAll('.quest');
    text.init(view, quests);
});

const text = (() => {
    let view;
    let quests;

    function init(_view, _quests) {
        view = _view;
        quests = _quests;

        for (let q of quests) {
            q.addEventListener('click', addToView);
        }

        // quests.addEventListener('click', addToView);
    }

    function el(type, className, clickHandler) {
        const newElement = document.createElement(type);
        newElement.classList.add(className);
        if (clickHandler === null)
            return newElement;
        newElement.addEventListener('click', clickHandler);
        return newElement;
    }

    function emptyView() {
        while (view.firstChild) {
            view.removeChild(view.firstChild);
        }   
        // view.removeChild(view.firstChild);
    }

    function addToView(e) {
        if (event.currentTarget !== event.target) {
            return;
          }
        
        const div = e.target;

        const cln = div.cloneNode(true);
        const h2 = document.createElement('h2');
        h2.innerHTML = 'Details'
        emptyView();
        // cln.children().show();
        // const view = document.createElement('div');
        const name = document.createElement('h1');
        name.innerHTML = cln.querySelector(".quest__name").innerHTML;
        name.className = "view__name";
        const xp = document.createElement('h2');
        xp.innerHTML = cln.querySelector(".quest__xp").innerHTML;
        xp.className = "view__xp";
        const coins = document.createElement('h2');
        coins.innerHTML = cln.querySelector(".quest__coins").innerHTML;
        coins.className = "view__coins";
        const deadline = document.createElement('h2');
        deadline.innerHTML = cln.querySelector(".quest__deadline").innerHTML;
        deadline.className = "view__deadline";
        const description = document.createElement('p');
        description.innerHTML = cln.querySelector(".quest__description").innerHTML;
        description.className = "view__description";
        const status = document.createElement('h2');
        status.innerHTML = cln.querySelector(".quest__status").innerHTML;
        status.className = "view__status";

        const assignToMe = document.createElement('button');
        assignToMe.innerHTML = 'Assign to me';
        assignToMe.className = 'view__assign';

        const markAsDone = document.createElement('button');
        markAsDone.innerHTML = 'Done';
        markAsDone.className = 'view__done';

        const id = document.createElement('input');
        id.value = cln.querySelector(".quest__id").value;
        id.style.visibility = 'hidden';

        const as = cln.getElementsByTagName('a');
        const assign = cln.querySelector('.assign');
        const done = cln.querySelector('.done');
        console.log(as.length);
        view.appendChild(h2);
        view.appendChild(name);
        view.appendChild(xp);
        view.appendChild(coins);
        view.appendChild(deadline);
        view.appendChild(description);
        view.appendChild(status);
        //view.appendChild(assignToMe);
        //view.appendChild(markAsDone);
        view.appendChild(id);
        //debugger

        if(assign) {
            assign.type = 'visible';
            assign.querySelector('.view__assign').style = "display: block;";
            view.appendChild(assign);
        }
        if(done) {
            done.type = 'visible';
            done.querySelector('.view__done').style = "display: block;";
            view.appendChild(done);
        }
    }
    return {
        init: init
    }

})();