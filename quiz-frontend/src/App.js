import React, { useState } from 'react';
import { BookOpen, Brain, CheckCircle, XCircle, Loader2 } from 'lucide-react';

export default function QuizApp() {
  const [step, setStep] = useState('select'); // select, quiz, results
  const [topic, setTopic] = useState('');
  const [level, setLevel] = useState('');
  const [questions, setQuestions] = useState([]);
  const [answers, setAnswers] = useState({});
  const [results, setResults] = useState(null);
  const [loading, setLoading] = useState(false);
  const [quizId, setQuizId] = useState(null);

  const topics = ['JavaScript', 'Python', 'Java', 'React', 'Spring Boot', 'Database', 'DSA', 'System Design'];
  const levels = ['Beginner', 'Intermediate', 'Advanced'];

  const generateQuiz = async () => {
    if (!topic || !level) {
      alert('Please select both topic and level');
      return;
    }

    setLoading(true);
    try {
      const response = await fetch('http://localhost:8081/api/quiz/generate', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ topic, level })
      });

      if (!response.ok) throw new Error('Failed to generate quiz');

      const data = await response.json();
      setQuestions(data.questions);
      setQuizId(data.quizId);
      setStep('quiz');
      setAnswers({});
    } catch (error) {
      alert('Error generating quiz: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleAnswerSelect = (questionIndex, optionIndex) => {
    setAnswers({ ...answers, [questionIndex]: optionIndex });
  };

  const submitQuiz = async () => {
    if (Object.keys(answers).length < questions.length) {
      alert('Please answer all questions before submitting');
      return;
    }

    setLoading(true);
    try {
      const response = await fetch('http://localhost:8081/api/quiz/submit', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ quizId, answers })
      });

      if (!response.ok) throw new Error('Failed to submit quiz');

      const data = await response.json();
      setResults(data);
      setStep('results');
    } catch (error) {
      alert('Error submitting quiz: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  const resetQuiz = () => {
    setStep('select');
    setTopic('');
    setLevel('');
    setQuestions([]);
    setAnswers({});
    setResults(null);
    setQuizId(null);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 p-6">
      <div className="max-w-4xl mx-auto">
        {/* Header */}
        <div className="text-center mb-8">
          <div className="flex items-center justify-center mb-4">
            <Brain className="w-12 h-12 text-indigo-600 mr-3" />
            <h1 className="text-4xl font-bold text-gray-800">AI Quiz Generator</h1>
          </div>
          <p className="text-gray-600">Test your knowledge with AI-generated questions</p>
        </div>

        {/* Topic & Level Selection */}
        {step === 'select' && (
          <div className="bg-white rounded-xl shadow-lg p-8">
            <h2 className="text-2xl font-semibold text-gray-800 mb-6 flex items-center">
              <BookOpen className="w-6 h-6 mr-2 text-indigo-600" />
              Select Quiz Configuration
            </h2>

            <div className="space-y-6">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Topic
                </label>
                <select
                  value={topic}
                  onChange={(e) => setTopic(e.target.value)}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                >
                  <option value="">Select a topic</option>
                  {topics.map((t) => (
                    <option key={t} value={t}>{t}</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Difficulty Level
                </label>
                <div className="grid grid-cols-3 gap-3">
                  {levels.map((l) => (
                    <button
                      key={l}
                      onClick={() => setLevel(l)}
                      className={`py-3 px-4 rounded-lg font-medium transition ${
                        level === l
                          ? 'bg-indigo-600 text-white'
                          : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                      }`}
                    >
                      {l}
                    </button>
                  ))}
                </div>
              </div>

              <button
                onClick={generateQuiz}
                disabled={loading}
                className="w-full bg-indigo-600 text-white py-3 rounded-lg font-semibold hover:bg-indigo-700 transition disabled:bg-gray-400 flex items-center justify-center"
              >
                {loading ? (
                  <>
                    <Loader2 className="w-5 h-5 mr-2 animate-spin" />
                    Generating Quiz...
                  </>
                ) : (
                  'Generate Quiz'
                )}
              </button>
            </div>
          </div>
        )}

        {/* Quiz Questions */}
        {step === 'quiz' && (
          <div className="bg-white rounded-xl shadow-lg p-8">
            <div className="mb-6">
              <h2 className="text-2xl font-semibold text-gray-800">{topic} Quiz</h2>
              <p className="text-gray-600">Level: {level}</p>
            </div>

            <div className="space-y-6">
              {questions.map((q, qIndex) => (
                <div key={qIndex} className="border-b border-gray-200 pb-6 last:border-0">
                  <h3 className="font-medium text-gray-800 mb-3">
                    {qIndex + 1}. {q.question}
                  </h3>
                  <div className="space-y-2">
                    {q.options.map((option, oIndex) => (
                      <button
                        key={oIndex}
                        onClick={() => handleAnswerSelect(qIndex, oIndex)}
                        className={`w-full text-left px-4 py-3 rounded-lg border-2 transition ${
                          answers[qIndex] === oIndex
                            ? 'border-indigo-600 bg-indigo-50'
                            : 'border-gray-200 hover:border-indigo-300'
                        }`}
                      >
                        <span className="font-medium mr-2">{String.fromCharCode(65 + oIndex)}.</span>
                        {option}
                      </button>
                    ))}
                  </div>
                </div>
              ))}
            </div>

            <button
              onClick={submitQuiz}
              disabled={loading}
              className="w-full mt-8 bg-green-600 text-white py-3 rounded-lg font-semibold hover:bg-green-700 transition disabled:bg-gray-400 flex items-center justify-center"
            >
              {loading ? (
                <>
                  <Loader2 className="w-5 h-5 mr-2 animate-spin" />
                  Submitting...
                </>
              ) : (
                'Submit Quiz'
              )}
            </button>
          </div>
        )}

        {/* Results */}
        {step === 'results' && results && (
          <div className="bg-white rounded-xl shadow-lg p-8">
            <div className="text-center mb-8">
              <div className={`inline-flex items-center justify-center w-20 h-20 rounded-full mb-4 ${
                results.score >= 7 ? 'bg-green-100' : results.score >= 5 ? 'bg-yellow-100' : 'bg-red-100'
              }`}>
                <span className={`text-3xl font-bold ${
                  results.score >= 7 ? 'text-green-600' : results.score >= 5 ? 'text-yellow-600' : 'text-red-600'
                }`}>
                  {results.score}/10
                </span>
              </div>
              <h2 className="text-3xl font-bold text-gray-800 mb-2">Quiz Complete!</h2>
              <p className="text-gray-600">
                {results.score >= 7 ? 'Excellent work!' : results.score >= 5 ? 'Good effort!' : 'Keep practicing!'}
              </p>
            </div>

            <div className="space-y-4 mb-8">
              {results.details.map((detail, index) => (
                <div key={index} className={`p-4 rounded-lg border-2 ${
                  detail.correct ? 'bg-green-50 border-green-200' : 'bg-red-50 border-red-200'
                }`}>
                  <div className="flex items-start">
                    {detail.correct ? (
                      <CheckCircle className="w-6 h-6 text-green-600 mr-3 mt-1 flex-shrink-0" />
                    ) : (
                      <XCircle className="w-6 h-6 text-red-600 mr-3 mt-1 flex-shrink-0" />
                    )}
                    <div className="flex-1">
                      <p className="font-medium text-gray-800 mb-2">{detail.question}</p>
                      <p className="text-sm text-gray-600">
                        <span className="font-medium">Your answer:</span> {detail.userAnswer}
                      </p>
                      {!detail.correct && (
                        <p className="text-sm text-gray-600">
                          <span className="font-medium">Correct answer:</span> {detail.correctAnswer}
                        </p>
                      )}
                    </div>
                  </div>
                </div>
              ))}
            </div>

            <button
              onClick={resetQuiz}
              className="w-full bg-indigo-600 text-white py-3 rounded-lg font-semibold hover:bg-indigo-700 transition"
            >
              Take Another Quiz
            </button>
          </div>
        )}
      </div>
    </div>
  );
}