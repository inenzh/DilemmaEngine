(() => {
  const screens = {
    start: document.getElementById('screen-start'),
    question: document.getElementById('screen-question'),
    results: document.getElementById('screen-results'),
  };

  const beginBtn = document.getElementById('begin-btn');
  const nextBtn = document.getElementById('next-btn');
  const restartBtn = document.getElementById('restart-btn');
  const qNumberEl = document.getElementById('q-number');
  const qTextEl = document.getElementById('q-text');
  const optionsEl = document.getElementById('options');
  const progressFill = document.getElementById('progress-fill');

  const ATTRIBUTE_LABELS = {
    MR: 'Moral Rigidity',
    ST: 'Strategic Thinking',
    RT: 'Risk Tolerance',
    EM: 'Empathy',
    PO: 'Power Orientation',
  };

  let sessionId = null;
  let currentQuestionId = null;
  let selectedChoice = null;

  function showScreen(name) {
    Object.values(screens).forEach(s => s.classList.add('hidden'));
    screens[name].classList.remove('hidden');
  }

  async function postForm(path, data) {
    const body = new URLSearchParams(data);
    const res = await fetch(path, { method: 'POST', body });
    if (!res.ok) {
      const err = await res.json().catch(() => ({ error: 'request failed' }));
      throw new Error(err.error || 'request failed');
    }
    return res.json();
  }

  function renderQuestion(payload) {
    sessionId = payload.sessionId;
    currentQuestionId = payload.question.id;
    selectedChoice = null;

    const num = String(payload.questionNumber).padStart(2, '0');
    qNumberEl.textContent = `DILEMMA ${num} / ${payload.total}`;
    progressFill.style.width = `${((payload.questionNumber - 1) / payload.total) * 100}%`;

    qTextEl.textContent = payload.question.text;

    optionsEl.innerHTML = '';
    const letters = ['A', 'B', 'C', 'D'];
    payload.question.options.forEach((text, i) => {
      const btn = document.createElement('button');
      btn.className = 'option';
      btn.type = 'button';
      btn.innerHTML = `<span class="letter">${letters[i]}</span><span>${escapeHtml(text)}</span>`;
      btn.addEventListener('click', () => selectOption(i, btn));
      optionsEl.appendChild(btn);
    });

    nextBtn.classList.add('hidden');
    showScreen('question');
  }

  function selectOption(index, btn) {
    selectedChoice = index;
    [...optionsEl.children].forEach(c => c.classList.remove('selected'));
    btn.classList.add('selected');
    nextBtn.classList.remove('hidden');
  }

  function renderResults(payload) {
    document.getElementById('archetype').textContent = payload.archetype;

    const list = document.getElementById('attribute-list');
    list.innerHTML = '';
    Object.keys(ATTRIBUTE_LABELS).forEach(key => {
      const score = payload.scores[key];
      const row = document.createElement('div');
      row.className = 'attribute-row';
      row.innerHTML = `
        <div class="attribute-head">
          <span class="attribute-name">${ATTRIBUTE_LABELS[key]}</span>
          <span class="mono-num">${score}</span>
        </div>
        <div class="progress-track"><div class="progress-fill" style="width:${score}%"></div></div>
        <p class="attribute-blurb">${escapeHtml(payload.blurbs[key])}</p>
      `;
      list.appendChild(row);
    });

    document.getElementById('consistency-value').textContent = `${payload.consistency}%`;
    document.getElementById('consistency-fill').style.width = `${payload.consistency}%`;
    document.getElementById('consistency-note').textContent = payload.consistencyNote;

    showScreen('results');
  }

  function escapeHtml(str) {
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
  }

  beginBtn.addEventListener('click', async () => {
    beginBtn.disabled = true;
    try {
      const payload = await postForm('/api/start', {});
      renderQuestion(payload);
    } catch (e) {
      alert('Could not start the assessment. Please try again.');
    } finally {
      beginBtn.disabled = false;
    }
  });

  nextBtn.addEventListener('click', async () => {
    if (selectedChoice === null) return;
    nextBtn.disabled = true;
    try {
      const payload = await postForm('/api/answer', {
        sid: sessionId,
        qid: currentQuestionId,
        choice: selectedChoice,
      });
      if (payload.done) {
        renderResults(payload);
      } else {
        renderQuestion(payload);
      }
    } catch (e) {
      alert('Something went wrong recording that answer. Please try again.');
    } finally {
      nextBtn.disabled = false;
    }
  });

  restartBtn.addEventListener('click', () => {
    sessionId = null;
    currentQuestionId = null;
    selectedChoice = null;
    showScreen('start');
  });
})();
