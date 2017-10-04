import Web_Crawler
import os

keywords = 'pangolin tree'
keywords2 = 'knuckle sandwich'
crawler = Web_Crawler.Spider()
crawler.get_start_page(keywords)
crawler.write_page_info(limit=30, max_page_number=1000)
