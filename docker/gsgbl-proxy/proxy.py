import flask
import requests
import os
import time

app = flask.Flask(__name__)

@app.route('/', methods=["POST"])

def proxy():
	form_data = flask.request.form
	if 'query' not in form_data:
		return 'No query in form data'
	for key, value in form_data.items():
		if key == 'query':
			continue
		with open('/kobe/dataset/gsgbl/'+key, "w") as file:
			file.write(value)
	r = requests.post('http://tmp-strabon.default.svc.cluster.local:8080/strabon/Query', data={'query': form_data['query']})
	print('waiting for /kobe/dataset/gsgbl/.dataLoaded')
	while not os.path.exists('/kobe/dataset/gsgbl/.dataLoaded'):
		time.sleep(5)
	return r.text

if __name__ == '__main__':
	app.run(host='localhost', port=5001, debug=True)
