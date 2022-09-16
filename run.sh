docker run \
    -v /data/downloader/conf/env.conf:/downloader/conf/conf.env:ro \
    --restart=always \
    --network="dex_default" \
    --name=downloader-scala \
    -d downloader_downloader:latest