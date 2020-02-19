# -*-coding:utf-8 -*-
import os

import sys

curPath = os.path.abspath(os.path.dirname(__file__))
rootPath = os.path.split(curPath)[0]
sys.path.append(rootPath)

import logging
import os
import platform
from typing import Union

import sys
import tornado.escape
import tornado.ioloop
import tornado.web
import tornado.websocket
from tornado.options import define, options

from kbinxml_util import KBinXML

if platform.system() == "Windows":
    import asyncio

    asyncio.set_event_loop_policy(asyncio.WindowsSelectorEventLoopPolicy())

define('port', default=50000, help="鸟居加密链接", type=int)


class Torii_WebSocketServer(tornado.websocket.WebSocketHandler):

    def open(self, *args: str, **kwargs: str):
        logging.info(f":鸟居WebSocket连接启动！！！")

    def on_message(self, message: Union[str, bytes]):
        try:

            if len(message) == 1 and message == b'\x00':
                # 拿到 1 个 0字节 数据，则代表结束请求直接关闭
                logging.info("接收到关闭指令")
                self.close(reason="主动关闭")
                tornado.ioloop.IOLoop.current().stop()

            if KBinXML.is_binary_xml(message):
                # 拿到的是 KBIN 数据，则转成 XML
                response_xml = KBinXML(message).to_text()
            else:
                # 拿到的是 XML 数据，则转成 KBIN
                if message[0] == 1:
                    response_xml = KBinXML(message[1:len(message)]).to_binary(encoding="UTF-8")
                elif message[0] == 2:
                    response_xml = KBinXML(message[1:len(message)]).to_binary(encoding="cp932")

            self.write_message(response_xml, True)

        except Exception as e:
            logging.info(e)
            logging.info(f"加密时出现了一丶问题")
            self.close(reason="请求出现异常")

    def on_close(self) -> None:
        logging.info(f":鸟居WebSocket关闭")

    def check_origin(self, origin: str) -> bool:
        return True


class Application(tornado.web.Application):
    def __init__(self, handler, setting):
        super(Application, self).__init__(handler, **setting)


def main():
    options.parse_command_line()
    # ws url设定
    handlers = [(r"/remote_kbin", Torii_WebSocketServer)]
    setting = dict(xsrf_cookies=False)
    app = Application(handlers, setting)
    # print(options.port)
    app.listen(options.port)
    tornado.ioloop.IOLoop.current().start()


if __name__ == '__main__':
    curPath = os.path.abspath(os.path.dirname(__file__))
    rootPath = os.path.split(curPath)[0]
    sys.path.append(rootPath)
    main()
