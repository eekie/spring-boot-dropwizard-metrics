docker build -t eekie/dropwizard-metrics -f docker/Dockerfile --rm=true --force-rm=true .

docker run -it --rm eekie/dropwizard-metrics

docker-compose -f docker/docker-compose.yml
